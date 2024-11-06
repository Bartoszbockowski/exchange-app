package com.java.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.exchange.dto.LoginRequestDTO;
import com.java.exchange.dto.RegisterRequestDTO;
import com.java.exchange.dto.TokenResponseDTO;
import com.java.exchange.security.controller.SecurityController;
import com.java.exchange.security.service.JwtService;
import com.java.exchange.security.service.UserService;
import com.java.exchange.appuser.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private static final String REGISTER_URL = "/api/v1/security/register";
    private static final String LOGIN_URL = "/api/v1/security/login";
    private static final String TEST_TOKEN = "ey123123eqwe1NiJ9.eyJzdWIiqweq324asregzA4MzgzMTd9.uxYq8CqweqetxfXxxasda8CRY";
    private static final String TEST_USERNAME = "j12384923kklas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequestDTO request = createRegisterRequest();
        TokenResponseDTO expectedResponse = createTokenResponse();

        doReturn(expectedResponse).when(authenticationService).register(request);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequestDTO request = createLoginRequest();
        TokenResponseDTO expectedResponse = createTokenResponse();

        doReturn(expectedResponse).when(authenticationService).login(request);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    private RegisterRequestDTO createRegisterRequest() {
        return RegisterRequestDTO.builder()
                .name("Jan")
                .lastName("Nowak")
                .password("password123")
                .startingBalancePln(BigDecimal.valueOf(300.0))
                .build();
    }

    private LoginRequestDTO createLoginRequest() {
        return LoginRequestDTO.builder()
                .username(TEST_USERNAME)
                .password("password123")
                .build();
    }

    private TokenResponseDTO createTokenResponse() {
        return TokenResponseDTO.builder()
                .username(TEST_USERNAME)
                .token(TEST_TOKEN)
                .build();
    }
}
