package com.example.bike.application.service.impl;

import java.util.ArrayList;
import java.util.List;

public class Page<C> {

    private final long totalElements;

    private final List<C> elementList = new ArrayList<>();

    public Page(long totalElements, List<C> elementList) {
        this.totalElements = totalElements;
        if (elementList != null) {
            this.elementList.addAll(elementList);
        }
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<C> getElementList() {
        return elementList;
    }

}