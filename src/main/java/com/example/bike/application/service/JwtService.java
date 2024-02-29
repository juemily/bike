package com.example.bike.application.service;

import com.example.bike.domain.model.LoginRequest;
import com.example.bike.domain.model.LoginResponse;
import org.springframework.security.core.Authentication;

public interface JwtService {

    LoginResponse authorize(LoginRequest login);
    LoginResponse refresh(Authentication auth, boolean rememberMe);

    LoginResponse buildToken(Authentication auth, boolean rememberMe);
}
