package com.example.bike.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponse {
    @NonNull
    private String accessToken;

    @NonNull
    private String refreshToken;
}
