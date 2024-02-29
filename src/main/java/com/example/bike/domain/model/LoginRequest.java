package com.example.bike.domain.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private boolean rememberMe;
}
