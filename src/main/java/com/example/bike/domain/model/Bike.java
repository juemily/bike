package com.example.bike.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bike {

    private UUID id;
    @NotNull
    private String name;
    private String description;
    private Double price;
    private Double manufacturer;
    private List<Items> items;


}
