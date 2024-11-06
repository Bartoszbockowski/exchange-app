package com.java.exchange.errorhandling.handler;

import com.java.exchange.errorhandling.enums.AppErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ExceptionResponse handleException(final Exception ex) {
        ExceptionResponse response = createExceptionResponse(ex);
        log.error("Exception ID: {}, ErrorCode: {}", response.getUuid(), response.getCode(), ex);
        return response;
    }

    private ExceptionResponse createExceptionResponse(Exception ex) {
        String errorCode = AppErrorCodes.UNKNOWN_ERROR.name();
        return new ExceptionResponse(errorCode, ex.getMessage());
    }

    @RequiredArgsConstructor
    @Getter
    public static class ExceptionResponse {
        private final String code;
        private final String message;
        private final OffsetDateTime timestamp = OffsetDateTime.now();
        private final String uuid = UUID.randomUUID().toString();
    }
}
