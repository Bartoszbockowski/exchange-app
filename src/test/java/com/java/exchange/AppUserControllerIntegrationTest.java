package com.java.exchange;

import com.java.exchange.appuser.enums.Currency;
import com.java.exchange.appuser.model.AppUser;
import com.java.exchange.appuser.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppUserControllerIntegrationTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser2024";
    private static final String TEST_NAME = "Jan";
    private static final String TEST_LAST_NAME = "Nowak";
    private static final BigDecimal INITIAL_USD_BALANCE = BigDecimal.ZERO;
    private static final BigDecimal INITIAL_PLN_BALANCE = BigDecimal.valueOf(1500.00);
    private static final BigDecimal EXCHANGE_AMOUNT = BigDecimal.valueOf(200.00);
    private static final BigDecimal EXPECTED_PLN_BALANCE = INITIAL_PLN_BALANCE.subtract(EXCHANGE_AMOUNT);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        AppUser appUser = buildTestUser();
        userRepository.save(appUser);
    }

    @Test
    void shouldGetDetails() throws Exception {
        mockMvc.perform(get("/api/v1/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(buildTestUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.accountBalancePln").value(INITIAL_PLN_BALANCE.doubleValue()))
                .andExpect(jsonPath("$.accountBalanceUsd").value(INITIAL_USD_BALANCE.doubleValue()));
    }

    @Test
    void shouldExchangeCurrencyAndUpdateDatabase() throws Exception {
        mockMvc.perform(post("/api/v1/appuser/exchange")
                        .param("currency", Currency.PLN.name())
                        .param("amount", EXCHANGE_AMOUNT.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(buildTestUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.accountBalancePln").value(EXPECTED_PLN_BALANCE.doubleValue()));

        AppUser appUser = userRepository.findById(TEST_USER_ID).orElseThrow();

        assertAll(
                () -> assertTrue(EXPECTED_PLN_BALANCE.compareTo(appUser.getAccountBalancePln()) == 0, "PLN balance should match expected value after exchange"),
                () -> assertNotEquals(appUser.getAccountBalanceUsd(), INITIAL_USD_BALANCE, "USD balance should be updated after exchange")
        );
    }

    private AppUser buildTestUser() {
        return AppUser.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .name(TEST_NAME)
                .lastName(TEST_LAST_NAME)
                .accountBalanceUsd(INITIAL_USD_BALANCE)
                .accountBalancePln(INITIAL_PLN_BALANCE)
                .password("securePass2024")
                .version(1L)
                .build();
    }
}
