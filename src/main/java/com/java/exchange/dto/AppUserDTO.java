package com.java.exchange.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AppUserDTO {
    private String name;
    private String lastName;
    private String username;
    private BigDecimal accountBalancePln;
    private BigDecimal accountBalanceUsd;
}
