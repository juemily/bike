package com.example.bike.inftrastructure.rest.api;


import com.example.bike.domain.error.exceptions.BaseException;
import com.example.bike.domain.model.LoginRequest;
import com.example.bike.domain.model.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value = "/bkool/bike/v1/auth")
public interface AuthApi {

    @PostMapping("/authenticate")
    ResponseEntity<LoginResponse> authorize(@Validated @RequestBody LoginRequest login) throws BaseException;

    @PostMapping("/refresh")
    ResponseEntity<LoginResponse> refresh(Authentication auth, @RequestParam(required = false) boolean rememberMe);
}
