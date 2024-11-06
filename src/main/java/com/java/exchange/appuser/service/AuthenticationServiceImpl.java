package com.java.exchange.appuser.service;

import com.java.exchange.dto.LoginRequestDTO;
import com.java.exchange.dto.RegisterRequestDTO;
import com.java.exchange.dto.TokenResponseDTO;
import com.java.exchange.security.service.JwtService;
import com.java.exchange.appuser.model.AppUser;
import com.java.exchange.appuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final int ACCOUNT_IDENTIFIER_SUFFIX_LIMIT = 10000;

    @Override
    public TokenResponseDTO register(final RegisterRequestDTO request) {
        String accountIdentifier = generateUniqueAccountIdentifier(request.getName(), request.getLastName());

        AppUser newUser = AppUser.builder()
                .username(accountIdentifier)
                .name(request.getName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountBalancePln(request.getStartingBalancePln())
                .accountBalanceUsd(BigDecimal.ZERO)
                .build();

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);

        return createTokenResponse(jwtToken, accountIdentifier);
    }

    @Override
    public TokenResponseDTO login(final LoginRequestDTO request) {
        authenticateUser(request.getUsername(), request.getPassword());

        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        String jwtToken = jwtService.generateToken(user);

        return createTokenResponse(jwtToken, user.getUsername());
    }

    private String generateUniqueAccountIdentifier(String name, String lastName) {
        String accountIdentifier;
        do {
            accountIdentifier = createAccountIdentifier(name, lastName);
        } while (userRepository.findByUsername(accountIdentifier).isPresent());
        return accountIdentifier;
    }

    private String createAccountIdentifier(String name, String lastName) {
        Random random = new Random();
        String namePrefix = name.length() > 1 ? name.substring(0, 2).toLowerCase() : name.toLowerCase();
        String lastNamePrefix = lastName.length() > 1 ? lastName.substring(0, 2).toLowerCase() : lastName.toLowerCase();
        int randomNumber = 1000 + random.nextInt(9000);  // Random 4-digit number for more uniqueness
        long timestamp = System.currentTimeMillis() % 100000; // Last 5 digits of current timestamp

        return namePrefix + lastNamePrefix + randomNumber + timestamp;
    }


    private void authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authToken);
    }

    private TokenResponseDTO createTokenResponse(String token, String username) {
        return TokenResponseDTO.builder()
                .token(token)
                .username(username)
                .build();
    }
}
