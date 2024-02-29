package com.example.bike.inftrastructure.mapper;

import com.example.bike.domain.model.Bike;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BikeMapper {

    BikeMapper INSTANCE = Mappers.getMapper(BikeMapper.class);

    Bike toDto(BikeEntity entity);
    Bike toDto(BikeEntity entity, Set<String> attributesToMap);

    BikeEntity toEntity(Bike resouce );


}
