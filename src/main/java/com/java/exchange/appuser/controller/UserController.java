package com.java.exchange.appuser.controller;

import com.java.exchange.dto.AppUserDTO;
import com.java.exchange.appuser.enums.Currency;
import com.java.exchange.appuser.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/appuser")
@RequiredArgsConstructor
public class UserController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<AppUserDTO> getUserDetails() {
        AppUserDTO userDetails = accountService.getDetails();
        return ResponseEntity.ok(userDetails); // Returns 200 OK with user details
    }

    @PostMapping("/exchange")
    public ResponseEntity<AppUserDTO> exchangeCurrency(@RequestParam Currency currency,
                                                       @RequestParam BigDecimal amount) {
        AppUserDTO updatedUserDetails = accountService.exchangeCurrency(currency, amount);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserDetails); // Explicit 200 OK response
    }
}
