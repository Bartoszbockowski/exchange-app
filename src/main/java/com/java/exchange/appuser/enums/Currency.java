package com.java.exchange.appuser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    PLN("pln"),
    USD("usd");

    private final String value;
}
