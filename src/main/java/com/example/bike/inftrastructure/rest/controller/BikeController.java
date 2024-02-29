package com.example.bike.inftrastructure.rest.controller;

import com.example.bike.application.service.BikeService;
import com.example.bike.application.service.impl.Page;
import com.example.bike.domain.exceptions.BikeException;
import com.example.bike.domain.model.Bike;
import com.example.bike.inftrastructure.rest.Api.BikeApi;
import com.example.bike.inftrastructure.rest.controller.pagination.HttpHeadersBuilder;
import com.example.bike.inftrastructure.rest.controller.pagination.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class BikeController implements BikeApi {

    private final BikeService service;

    private static final int DEFAULT_PAGINATION_OFFSET = 0;

    private static final int DEFAULT_PAGINATION_LIMIT = 20;

    private final Set<String> queryParamNames = Set.of("offset", "limit", "fields", "sort");

    @Autowired
    public BikeController(BikeService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Bike> createBike(Bike bike) throws BikeException {
        log.debug("Create Bike Controller");
         return new ResponseEntity<>(service.create(bike), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Bike>>  list(@RequestHeader(required = false, name = "Range") String requestRange,
                                            @RequestParam Map<String, String> allRequestParams,
                                            @RequestParam(value = "fields", required = false) String fields,
                                            @RequestParam(value = "offset", required = false) Integer offset,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "sort", required = false) String sort)throws BikeException {

        int queryLimit = DEFAULT_PAGINATION_LIMIT;
        int queryOffset = DEFAULT_PAGINATION_OFFSET;
        Set<String> attributesToMap = null;
        List<String> sortValues = null;
        Range range = null;
        HttpHeaders httpHeaders = null;
        HttpStatus status = HttpStatus.OK;

        if (limit != null && offset != null) {
            queryLimit = limit;
            queryOffset = offset;
        } else if (StringUtils.isNotEmpty(requestRange)) {
            String[] split = requestRange.split("=");
            String name = split[0];
            String[] splitItems = split[1].split("-");
            int rangeInit = Integer.parseInt(splitItems[0]);
            int rangeFinish = Integer.parseInt(splitItems[1]);
            range = new Range(name, rangeInit, rangeFinish);
            queryOffset = rangeInit;
            queryLimit = rangeFinish - rangeInit;
        } else if (offset != null) {
            queryOffset = offset;
        } else if (limit != null) {
            queryLimit = limit;
        }

        if (StringUtils.isNotBlank(fields)) {
            attributesToMap = Set.of(StringUtils.split(fields, ","));
        }

        if (StringUtils.isNotBlank(sort)) {
            sortValues = List.of(StringUtils.split(sort, ","));
        }

        for (String name : queryParamNames) {
            allRequestParams.remove(name);
        }


        Page<Bike> page = service.list(queryOffset, queryLimit, attributesToMap, sortValues, allRequestParams);

        if (range == null) {
            try {
                httpHeaders = HttpHeadersBuilder.generatePaginationHttpHeaders(queryOffset, queryLimit,
                        page.getTotalElements(),
                        ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString());
            } catch (URISyntaxException e) {
                log.error("Error generating http headers for request with params {}, {}", allRequestParams,
                        e.getMessage());
            }
            status = (queryLimit >= page.getTotalElements()) ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;
        } else {
            range.setTotalElements(page.getTotalElements());
            httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_RANGE, range.getRangeHeader());
        }

        return new ResponseEntity<>(page.getElementList(), httpHeaders, status);
    }
}
