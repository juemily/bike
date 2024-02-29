package com.example.bike.application.service.impl;

import com.example.bike.application.repository.BikeRepository;
import com.example.bike.application.service.BikeService;
import com.example.bike.application.service.filter.GenericSpecificationBuilder;
import com.example.bike.application.service.filter.SortBuilder;
import com.example.bike.domain.exceptions.BikeException;
import com.example.bike.domain.model.Bike;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import com.example.bike.inftrastructure.mapper.BikeMapper;
import com.example.bike.inftrastructure.rest.controller.pagination.OffsetBasedPageRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BikeServiceImpl  implements BikeService {

    private final BikeRepository repository;
    private final BikeMapper mapper;

    @Override
    public Bike create(Bike bike) throws BikeException {
        log.info("create Bike");
        BikeEntity response =  repository.saveAndFlush(mapper.toEntity(bike));
        return mapper.toDto(response);
    }

    @Override
    @Cacheable("bike")
    public Page<Bike> list(Integer offset, Integer limit, Set<String> fields, List<String> sort, Map<String, String> filter) throws BikeException {
        log.info("list Bike");
        SortBuilder sortBuilder = new SortBuilder();
        if (sort != null && !sort.isEmpty()) {
            for (String fieldName : sort) {
                sortBuilder.orderBy(fieldName);
            }
        }

        GenericSpecificationBuilder<BikeEntity> builder = new GenericSpecificationBuilder<>("");
        if (filter != null && !filter.isEmpty()) {
            for (String fieldName : filter.keySet()) {
                builder.with(fieldName, new String[]{filter.get(fieldName)});
            }
        }

        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, limit, sortBuilder.build());
        org.springframework.data.domain.Page<BikeEntity> entityPage = repository.findAll(builder.build(),
                pageRequest);
        List<Bike> resourceList = new ArrayList<>();
        entityPage.toList().forEach(item -> resourceList.add(mapper.toDto(item, fields)));

        log.debug(
                "LIST: Returning {} of {} ({} pages) Segmento items (offset={}, limit={}, fields={}, sort={}, filter={})",
                resourceList.size(), entityPage.getTotalElements(), entityPage.getTotalPages(), offset, limit, fields,
                sort, filter);

        return new Page<>(entityPage.getTotalElements(), resourceList);
    }


}
