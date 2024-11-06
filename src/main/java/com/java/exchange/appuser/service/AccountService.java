package com.java.exchange.appuser.service;

import com.java.exchange.dto.AppUserDTO;
import com.java.exchange.appuser.enums.Currency;

import java.math.BigDecimal;

public interface AccountService {
    AppUserDTO getDetails();
    AppUserDTO exchangeCurrency(Currency currency, BigDecimal amount);
}
