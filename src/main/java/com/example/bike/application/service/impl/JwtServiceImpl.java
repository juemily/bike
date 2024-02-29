package com.example.bike.application.service.impl;

import com.example.bike.application.service.JwtService;
import com.example.bike.domain.model.LoginRequest;
import com.example.bike.domain.model.LoginResponse;
import com.example.bike.inftrastructure.config.security.jwt.TokenProvider;
import com.example.bike.inftrastructure.config.security.ldap.LdapAuthenticationProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LdapAuthenticationProvider ldapAuthenticationProvider;
    private final TokenProvider tokenProvider;

    public JwtServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, LdapAuthenticationProvider ldapAuthenticationProvider, TokenProvider tokenProvider) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.ldapAuthenticationProvider = ldapAuthenticationProvider;
        this.tokenProvider = tokenProvider;
    }


    @Override
    @SneakyThrows
    public LoginResponse authorize(LoginRequest login) {
        log.info("Authenticating the user'{}'", login.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        return buildToken(auth, login.isRememberMe());

    }

    @Override
    public LoginResponse refresh(Authentication auth, boolean rememberMe) {
        log.info("refreshing userToken '{}'", auth.getName());
        UsernamePasswordAuthenticationToken authToken = ldapAuthenticationProvider.buildToken(auth.getName());

        return buildToken(authToken, rememberMe);

    }

    @Override
    public LoginResponse buildToken(Authentication auth, boolean rememberMe) {
        String accessToken = tokenProvider.createToken(auth, rememberMe);
        String refreshToken = tokenProvider.createRefreshToken(auth, rememberMe);
        return new LoginResponse(accessToken, refreshToken);
    }
}
