package com.example.bike.inftrastructure.mapper.impl;

import com.example.bike.domain.model.Bike;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import com.example.bike.inftrastructure.mapper.BikeMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BikeMapperImp extends GenericMapperImpl implements BikeMapper {
    @Autowired
    public BikeMapperImp(MapperAttributeFilter mapperAttributeFilter) {
        super(mapperAttributeFilter);
    }

    @Override
    public Bike toDto(BikeEntity entity) {
        return modelMapper.map(entity, Bike.class);
    }

    @Override
    public Bike toDto(BikeEntity entity, Set<String> attributesToMap) {
        ModelMapper mp = modelMapper(attributesToMap);
        return mp.map(entity, Bike.class);
    }

    @Override
    public BikeEntity toEntity(Bike resouce) {
        return modelMapper.map(resouce, BikeEntity.class);
    }

    @Override
    protected void addTypeMaps(ModelMapper modelMapper) {

    }
}
