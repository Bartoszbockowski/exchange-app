package com.java.exchange.security.service;

import com.java.exchange.appuser.model.AppUser;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;

import java.util.Objects;

public class SecurityContextHolder {

    public static AppUser getCurrentlyLoggedInUser() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AuthorizationServiceException("User not logged in");
        }
        return (AppUser) authentication.getPrincipal();
    }
}
