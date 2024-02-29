package com.example.bike.inftrastructure.config.security.helpers;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelper {
    private SecurityHelper() {
    }


    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
