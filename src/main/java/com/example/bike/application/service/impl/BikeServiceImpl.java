package com.example.bike.application.service.impl;

import com.example.bike.application.repository.BikeRepository;
import com.example.bike.application.service.BikeService;
import com.example.bike.application.service.filter.GenericSpecificationBuilder;
import com.example.bike.application.service.filter.SortBuilder;
import com.example.bike.domain.enums.ErrorDefinitionEnum;
import com.example.bike.domain.error.exceptions.BikeException;
import com.example.bike.domain.model.Bike;
import com.example.bike.domain.model.Items;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import com.example.bike.inftrastructure.mapper.BikeMapper;
import com.example.bike.inftrastructure.rest.controller.pagination.OffsetBasedPageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BikeServiceImpl implements BikeService {

    private final BikeRepository repository;
    private final BikeMapper mapper;

    @Override
    public Bike create(Bike bike) throws BikeException {
        log.info("create Bike");
        checkValues(bike);
        BikeEntity response = repository.saveAndFlush(mapper.toEntity(bike));
        return mapper.toDto(response);
    }

    @Override
    @Cacheable("bike")
    public Page<Bike> list(Integer offset, Integer limit, Set<String> fields, List<String> sort, Map<String, String> filter) throws BikeException {
        log.info("list Bike");
        List<Bike> resourceList = new ArrayList<>();

        SortBuilder sortBuilder = new SortBuilder();
        if (sort != null && !sort.isEmpty()) {
            sort.forEach(sortBuilder::orderBy);
        }

        GenericSpecificationBuilder<BikeEntity> builder = new GenericSpecificationBuilder<>("");
        if (filter != null && !filter.isEmpty()) {
            filter.forEach((fieldName, value) -> builder.with(fieldName, new String[]{value}));
        }

        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, limit, sortBuilder.build());
        org.springframework.data.domain.Page<BikeEntity> entityPage = repository.findAll(builder.build(), pageRequest);

        if(entityPage != null){
             resourceList = entityPage.stream()
                    .map(item -> mapper.toDto(item, fields))
                    .collect(Collectors.toList());
            return new Page<>(entityPage.getTotalElements(), resourceList);
        }
        return new Page<>(0L, resourceList);


    }

    void checkValues(Bike resource) throws BikeException {
        validateRequiredField(resource.getName(), "NAME");

        List<Items> items = resource.getItems();
        if (items != null && !items.isEmpty()) {

            for (Items item : items) {
                validateRequiredField(item.getModel(), "Items Model");
                validateRequiredField(item.getType(), "Items Type");
            }
        }
    }

    private void validateRequiredField(String value, String fieldName) throws BikeException {
        if (value == null || value.isBlank()) {
            throw new BikeException(ErrorDefinitionEnum.GENERIC_CREATE_ERROR,
                    "Falta campo requerido en " + fieldName + " o No tiene data",
                    HttpStatus.BAD_REQUEST.value());
        }
    }


}
