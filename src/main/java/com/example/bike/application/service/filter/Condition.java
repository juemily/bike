package com.example.bike.application.service.filter;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Data
public class Condition {

    private final String attribute;

    private final Operation operator;

    private final String value;

    public boolean isPath() {
        return StringUtils.contains(attribute, ".");
    }

    public String getPathAsString() {
        if (isPath()) {
            return attribute.substring(0, StringUtils.lastIndexOf(attribute, "."));
        } else {
            return "";
        }
    }

    public String[] getPath() {
        if (isPath()) {
            String[] pathElements = StringUtils.split(attribute, ".");
            return Arrays.copyOf(pathElements, pathElements.length - 1);
        } else {
            return new String[0];
        }
    }

    public String getAttributeName() {
        if (isPath()) {
            String[] pathElements = StringUtils.split(attribute, ".");
            return pathElements[pathElements.length - 1];
        } else {
            return attribute;
        }
    }

}
