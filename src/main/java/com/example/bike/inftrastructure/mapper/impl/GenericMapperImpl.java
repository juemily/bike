package com.example.bike.inftrastructure.mapper.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GenericMapperImpl {

    protected final MapperAttributeFilter mapperAttributeFilter;

    private final Map<String, ModelMapper> cache = new HashMap<>();

    protected final ModelMapper modelMapper;

    public GenericMapperImpl(MapperAttributeFilter mapperAttributeFilter) {
        this.mapperAttributeFilter = mapperAttributeFilter;
        this.modelMapper = modelMapper();
    }

    protected abstract void addTypeMaps(ModelMapper modelMapper);

    protected ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        addTypeMaps(modelMapper);
        return modelMapper;
    }

    protected ModelMapper modelMapper(Set<String> attributesToMap) {
        if (attributesToMap != null && !attributesToMap.isEmpty()) {
            String key = attributesToMap.stream().sorted().collect(Collectors.joining());
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            ModelMapper modelMapper = modelMapper();
            modelMapper.getConfiguration().setPropertyCondition(mappingContext -> mapperAttributeFilter.mapAttribute(mappingContext, attributesToMap));
            cache.put(key, modelMapper);
            return modelMapper;
        }
        return modelMapper;
    }

}
