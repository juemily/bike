package com.example.bike.application.service.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortBuilder {

    private static class SortItem {
        Sort.Direction direction;
        String field;
    }

    private final List<SortItem> orderCriteria = new ArrayList<>();

    public void orderBy(String field) {
        if(field != null) {
            SortItem sItem = new SortItem();
            sItem.direction = (field.startsWith("-")? Sort.Direction.DESC : Sort.Direction.ASC);
            sItem.field = (StringUtils.startsWithAny(field, "-", "+") ? field.substring(1) : StringUtils.trim(field));
            orderCriteria.add(sItem);
        }
    }

    public Sort build() {
        if(orderCriteria.isEmpty()) {
            return Sort.unsorted();
        } else {
            Sort sortCriteria = Sort.by(orderCriteria.get(0).direction, orderCriteria.get(0).field);
            for (int i = 1; i < orderCriteria.size(); i++) {
                sortCriteria = sortCriteria.and(Sort.by(orderCriteria.get(i).direction, orderCriteria.get(i).field));
            }
            return sortCriteria;
        }
    }

}
