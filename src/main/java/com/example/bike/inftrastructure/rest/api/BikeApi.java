package com.example.bike.inftrastructure.rest.api;

import com.example.bike.domain.error.exceptions.BaseException;
import com.example.bike.domain.model.Bike;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequestMapping(value = "/bkool/bike/v1")
public interface BikeApi {

    @PostMapping(value = "/bike", produces = {"application/json;charset=utf-8"}, consumes = {
            "application/json;charset=utf-8"})
    ResponseEntity<Bike> createBike(@RequestBody Bike bike) throws BaseException;

    @GetMapping(value = "/bike",  produces = {"application/json;charset=utf-8"})
    ResponseEntity<List<Bike>> list(@RequestHeader(required = false, name = "Range") String requestRange,
                                    @RequestParam Map<String, String> allRequestParams,
                                    @RequestParam(value = "fields", required = false) String fields,
                                    @RequestParam(value = "offset", required = false) Integer offset,
                                    @RequestParam(value = "limit", required = false) Integer limit,
                                    @RequestParam(value = "sort", required = false) String sort) throws BaseException;
}
