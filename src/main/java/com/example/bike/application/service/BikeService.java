package com.example.bike.application.service;

import com.example.bike.application.service.impl.Page;
import com.example.bike.domain.error.exceptions.BikeException;
import com.example.bike.domain.model.Bike;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BikeService {

    Bike create(Bike bike) throws BikeException;
    Page<Bike> list(Integer offset, Integer limit, Set<String> fields, List<String> sort, Map<String, String> filter) throws BikeException;
}
