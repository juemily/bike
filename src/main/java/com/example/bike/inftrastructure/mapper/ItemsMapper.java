package com.example.bike.inftrastructure.mapper;

import com.example.bike.domain.model.Bike;
import com.example.bike.domain.model.Items;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import com.example.bike.inftrastructure.dbo.ItemsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemsMapper {

    ItemsMapper INSTANCE = Mappers.getMapper(ItemsMapper.class);

    Items toDto(ItemsEntity entity);

    ItemsEntity toEntity(Items resouce );
}
