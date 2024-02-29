package com.example.bike.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Items {

    private UUID id;
    @NotNull
    private String model;
    @NotNull
    private String type;
    private String description;

}
