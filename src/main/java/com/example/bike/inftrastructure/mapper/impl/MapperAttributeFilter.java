package com.example.bike.inftrastructure.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class MapperAttributeFilter {

    private static final Set<String> atAttributes = new HashSet<>(Arrays.asList("type", "baseType", "schemaLocation", "referredType"));

    public boolean mapAttribute(MappingContext<Object, Object> mappingContext, Set<String> attributesToMap) {
        try {
            //This is an ugly trick, read a protected field using refection, but is the only way to know the
            //complete path for the attribute we are processing
            String destinationPath = (String) FieldUtils.readField(mappingContext, "destinationPath", true);
            String attName = StringUtils.substringBeforeLast(destinationPath, ".");
            attName = processAtAttribute(attName);

            //Id and hrefmust be always.
            //No filters, then all attributes must be mapped.
            //If the attribute is in the list, then map it.
            if (StringUtils.equals(attName, "id") || StringUtils.equals(attName, "href") ||attributesToMap.isEmpty() || attributesToMap.contains(attName)) {
                return true;
            }

            //Always map boolean values because jackson always serialize to json the boolean attributes, so we need
            //these attributes have the right values.
            if (isMappingToBoolean(mappingContext.getDestinationType())) {
                return true;
            }

            if (containsFilterStartingWith(attributesToMap, attName)) {
                return true;
            }

            //We always must return the id attribute if exists. So, if the attribute we are processing is the id attribute and
            //we have any field starting with the attribute prefix, then we must show it.
            //Ex: we are processing attribute orderItem.service.id. If we have the field orderItem.service, then the id must be shown, but if
            //    we are not orderItem.service in the attributes list, then the id must not be shown because que don't want the orderItem.service
            //    in the output.
            if (StringUtils.endsWith(attName, ".id") && containsFilterStartingWith(attributesToMap, StringUtils.substringBeforeLast(attName, "."))) {
                return true;
            }

            String[] subPaths = calculateSubPaths(attName);
            if (containsFilterEqualsToAnyOf(attributesToMap, subPaths)) {
                return true;
            }

            return isAnySubPathGreaterThanAttName(attributesToMap, attName);

        } catch (IllegalAccessException e) {
            log.error("Error accessing field destinationPath: {}", e.getMessage());
        }

        //If something goes wrong, then map the attribute by default.
        return true;
    }

    private boolean isMappingToBoolean(Class<?> classz) {
        return StringUtils.equalsIgnoreCase("boolean", classz.getName());
    }

    private String processAtAttribute(String att) {
        String result = att;

        if (atAttributes.contains(att)) {
            result = "@" + att;
        } else if (atAttributes.contains(StringUtils.substringAfterLast(att, "."))) {
            result = StringUtils.substringBeforeLast(att, ".") + ".@" + StringUtils.substringAfterLast(att, ".");
        }

        return result;
    }

    private boolean isAnySubPathGreaterThanAttName(Set<String> atts, String attName) {
        if (atts != null && attName != null) {
            for (String att : atts) {
                String[] subPaths = calculateSubPaths(att);
                for (String path : subPaths) {
                    if (path.startsWith(attName) && StringUtils.countMatches(path, ".") > StringUtils.countMatches(attName, "."))
                        return true;
                }
            }
        }
        return false;
    }

    private String[] calculateSubPaths(String path) {
        String[] subPaths = new String[StringUtils.countMatches(path, ".")];
        int end = 0;
        for (int i = 0; i < subPaths.length; i++) {
            end = path.indexOf(".", end + 1);
            subPaths[i] = path.substring(0, end);
        }
        return subPaths;
    }

    private boolean containsFilterStartingWith(Set<String> atts, String prefix) {
        if (atts != null && prefix != null) {
            for (String att : atts) {
                if (att.startsWith(prefix) && (StringUtils.countMatches(att, ".") > StringUtils.countMatches(prefix, "."))) return true;
            }
        }
        return false;
    }

    private boolean containsFilterEqualsToAnyOf(Set<String> atts, String[] strs) {
        if (atts != null && strs != null) {
            for (String att : atts) {
                if (StringUtils.equalsAny(att, strs)) return true;
            }
        }
        return false;
    }

}

