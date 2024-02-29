package com.example.bike.application.service.impl;

import com.example.bike.application.repository.BikeRepository;
import com.example.bike.application.service.filter.GenericSpecificationBuilder;
import com.example.bike.application.service.filter.SortBuilder;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BikeServiceImplTest {
    @Mock
    private BikeRepository repository;

    @Mock
    private BikeMapper mapper;

    @InjectMocks
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
    void testCreateBike() throws BikeException {

        when(mapper.toEntity(inputBike)).thenReturn(expectedEntity);
        when(repository.saveAndFlush(expectedEntity)).thenReturn(expectedEntity);
        when(mapper.toDto(expectedEntity)).thenReturn(inputBike);

        // Act
        Bike result = bikeService.create(inputBike);

        // Assert
        assertNotNull(result);
        assertEquals(inputBike, result);

        // Verify interactions
        verify(mapper).toEntity(inputBike);
        verify(repository).saveAndFlush(expectedEntity);
        verify(mapper).toDto(expectedEntity);
    }

    @Test
    void testListBike() throws BikeException {

        Integer offset = 0;
        Integer limit = 10;
        SortBuilder sortBuilder = new SortBuilder();
        GenericSpecificationBuilder<BikeEntity> builder = new GenericSpecificationBuilder<>("");
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, limit, sortBuilder.build());
        Set<String> fields = new HashSet<>(Arrays.asList("field1", "field2"));
        List<String> sort = Arrays.asList("field1", "field2");
        Map<String, String> filter = new HashMap<>();
        filter.put("fieldName1", "value1");
        filter.put("fieldName2", "value2");


        List<BikeEntity> bikeEntities = new ArrayList<>();
        bikeEntities.add(new BikeEntity());
        bikeEntities.add(new BikeEntity());


        Pageable pageable = Pageable.ofSize(10).withPage(0); // Tamaño de página 10, página 0 (primera página)


        org.springframework.data.domain.Page<BikeEntity> entityPage = new PageImpl<>(bikeEntities, pageable, bikeEntities.size());


        when(repository.findAll(builder.build(), pageRequest)).thenReturn(entityPage);
        when(mapper.toDto(any(), any())).thenReturn(new Bike());

        // When
        com.example.bike.application.service.impl.Page<Bike> result = bikeService.list(offset, limit, fields, sort, filter);

        // Then
        assertEquals(0, result.getElementList().size()); // Aseguramos que el tamaño del contenido coincida

    }

    @Test
    void testCheckValues() throws BikeException {

        bikeService.checkValues(inputBike);

    }
}