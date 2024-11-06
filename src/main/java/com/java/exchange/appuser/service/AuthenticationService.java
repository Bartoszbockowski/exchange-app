package com.java.exchange.appuser.service;

import com.java.exchange.dto.LoginRequestDTO;
import com.java.exchange.dto.RegisterRequestDTO;
import com.java.exchange.dto.TokenResponseDTO;

public interface AuthenticationService {
    TokenResponseDTO register(RegisterRequestDTO request);

    TokenResponseDTO login(LoginRequestDTO request);
}
