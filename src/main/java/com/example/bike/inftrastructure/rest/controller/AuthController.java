package com.example.bike.inftrastructure.rest.controller;

import com.example.bike.application.service.impl.JwtServiceImpl;
import com.example.bike.domain.error.exceptions.BaseException;
import com.example.bike.domain.model.LoginRequest;
import com.example.bike.domain.model.LoginResponse;
import com.example.bike.inftrastructure.rest.api.AuthApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController implements AuthApi {
    @Autowired
    private JwtServiceImpl jwtService;


    @Override
    public ResponseEntity<LoginResponse> authorize(LoginRequest login) throws BaseException {
        return ResponseEntity.ok(jwtService.authorize(login));
    }

    @Override
    public ResponseEntity<LoginResponse> refresh(Authentication auth, boolean rememberMe) {
        return ResponseEntity.ok(jwtService.refresh(auth,rememberMe));
    }
}
