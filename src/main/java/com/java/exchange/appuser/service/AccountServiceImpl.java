package com.java.exchange.appuser.service;

import com.java.exchange.appuser.enums.Currency;
import com.java.exchange.appuser.mapper.AppUserMapper;
import com.java.exchange.appuser.model.AppUser;
import com.java.exchange.appuser.repository.UserRepository;
import com.java.exchange.common.util.WebApiCaller;
import com.java.exchange.dto.AppUserDTO;
import com.java.exchange.security.service.SecurityContextHolder;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AppUserMapper appUserMapper;
    private final WebApiCaller webApiCaller;

    @Value("${currency.api.nbp.url}")
    private String apiUrlTemplate;

    @Override
    public AppUserDTO getDetails() {
        Long userId = SecurityContextHolder.getCurrentlyLoggedInUser().getId();
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return appUserMapper.toDto(user);
    }

    @Override
    @Transactional
    public AppUserDTO exchangeCurrency(Currency sourceCurrency, BigDecimal amount) {
        validateCurrency(sourceCurrency);
        validateAmount(amount);

        Long userId = SecurityContextHolder.getCurrentlyLoggedInUser().getId();
        AppUser appUser = userRepository.getReferenceById(userId);

        BigDecimal currencyRate = getCurrencyRate(Currency.USD);
        Currency targetCurrency = getTargetCurrency(sourceCurrency);
        BigDecimal convertedAmount = calculateConvertedAmount(sourceCurrency, amount, currencyRate);

        verifySufficientFunds(appUser, sourceCurrency, amount);
        try {
            updateAccountBalances(appUser, sourceCurrency, targetCurrency, amount, convertedAmount);
            userRepository.save(appUser);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Account balance was modified by another transaction. Please try again.", e);
        }
        return appUserMapper.toDto(appUser);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateCurrency(Currency currency) {
        if (!EnumSet.of(Currency.USD, Currency.PLN).contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }

    private Currency getTargetCurrency(Currency currency) {
        return currency == Currency.USD ? Currency.PLN : Currency.USD;
    }

    private BigDecimal calculateConvertedAmount(Currency sourceCurrency, BigDecimal amount, BigDecimal currencyRate) {
        return sourceCurrency == Currency.USD
                ? amount.multiply(currencyRate)
                : amount.divide(currencyRate, RoundingMode.HALF_UP);
    }

    private void verifySufficientFunds(AppUser appUser, Currency currency, BigDecimal amount) {
        BigDecimal currentBalance = getAccountBalance(appUser, currency);
        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for currency exchange");
        }
    }

    private BigDecimal getAccountBalance(AppUser appUser, Currency currency) {
        return currency == Currency.USD ? appUser.getAccountBalanceUsd() : appUser.getAccountBalancePln();
    }

    private void updateAccountBalances(AppUser appUser, Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount, BigDecimal targetAmount) {
        if (sourceCurrency == Currency.USD) {
            appUser.setAccountBalanceUsd(appUser.getAccountBalanceUsd().subtract(sourceAmount));
            appUser.setAccountBalancePln(appUser.getAccountBalancePln().add(targetAmount));
        } else {
            appUser.setAccountBalancePln(appUser.getAccountBalancePln().subtract(sourceAmount));
            appUser.setAccountBalanceUsd(appUser.getAccountBalanceUsd().add(targetAmount));
        }
    }

    private BigDecimal getCurrencyRate(Currency currency) {
        String apiUrl = buildApiUrl(currency);
        JSONObject jsonResponse = new JSONObject(webApiCaller.getObject(apiUrl, String.class));
        return extractCurrencyRate(jsonResponse);
    }

    private String buildApiUrl(Currency currency) {
        return apiUrlTemplate.replace("{currency}", currency.getValue());
    }

    private BigDecimal extractCurrencyRate(JSONObject jsonResponse) {
        return BigDecimal.valueOf(jsonResponse.getJSONArray("rates").getJSONObject(0).getDouble("mid"));
    }
}
