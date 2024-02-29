package com.example.bike.inftrastructure.rest.controller;


import com.example.bike.application.repository.BikeRepository;
import com.example.bike.application.service.filter.GenericSpecificationBuilder;
import com.example.bike.application.service.filter.SortBuilder;
import com.example.bike.application.service.impl.BikeServiceImpl;
import com.example.bike.application.service.impl.Page;
import com.example.bike.domain.error.exceptions.BaseException;
import com.example.bike.domain.error.exceptions.BikeException;
import com.example.bike.domain.model.Bike;
import com.example.bike.domain.model.Items;
import com.example.bike.inftrastructure.dbo.BikeEntity;
import com.example.bike.inftrastructure.dbo.ItemsEntity;
import com.example.bike.inftrastructure.mapper.BikeMapper;
import com.example.bike.inftrastructure.rest.controller.pagination.OffsetBasedPageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BikeControllerTest {
    @InjectMocks
    private BikeController bikeController;

    @Mock
    private BikeServiceImpl bikeService;

    private Items inputItems = new Items();
    private List<Items> itemList = new ArrayList<>();
    private List<Bike> bikeList = new ArrayList<>();
    private Bike inputBike = new Bike();

    private BikeEntity expectedEntity = new BikeEntity();

    private List<BikeEntity> listEntity = new ArrayList<>();

    private BikeEntity bikeEntity = new BikeEntity();

    private List<ItemsEntity> itemsEntityList = new ArrayList<>();

    private ItemsEntity itemsEntity = new ItemsEntity();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        inputItems.setModel("model");
        inputItems.setType("type");
        inputItems.setDescription("description");
        itemList.add(inputItems);
        // Arrange

        inputBike.setItems(itemList);
        inputBike.setName("nombre");
        inputBike.setPrice(100.0);
        inputBike.setManufacturer(3.0);
        inputBike.setDescription("Go Team Bike!");

        bikeList.add(inputBike);


        // cargo objeto de entidades
        itemsEntity.setModel("model");
        itemsEntity.setType("type");
        itemsEntity.setDescription("description");
        itemsEntityList.add(itemsEntity);

        bikeEntity.setItems(itemsEntityList);
        bikeEntity.setName("nombre");
        bikeEntity.setPrice(100.0);
        bikeEntity.setManufacturer(3.0);
        bikeEntity.setDescription("Go Team Bike!");
        listEntity.add(bikeEntity);
        listEntity.add(bikeEntity);


    }

    @Test
    void createBike() throws BaseException {

        when(bikeService.create(inputBike)).thenReturn(inputBike);

        // Act
        ResponseEntity<Bike> response = bikeController.createBike(inputBike);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inputBike, response.getBody());
    }


}