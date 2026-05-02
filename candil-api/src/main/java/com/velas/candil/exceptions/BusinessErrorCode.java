package com.velas.candil.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {

    // AUTH
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "User not found"),
    INVALID_CREDENTIALS(401, HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    ACCOUNT_LOCKED(423, HttpStatus.LOCKED, "Account is locked"),
    ACCOUNT_DISABLED(403, HttpStatus.FORBIDDEN, "Account is disabled"),
    TOKEN_EXPIRED(401, HttpStatus.UNAUTHORIZED, "Token expired"),
    TOKEN_INVALID(401, HttpStatus.UNAUTHORIZED, "Invalid token"),
    ROLE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Role not found"),
    USER_ALREADY_EXIST(400, HttpStatus.BAD_REQUEST, "User already exist"),

    // CANDLES
    CANDLE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Candle not found"),
    CANDLE_OUT_OF_STOCK(400, HttpStatus.BAD_REQUEST, "Candle out of stock"),
    INVALID_CANDLE_DATA(400, HttpStatus.BAD_REQUEST, "Invalid candle data"),

    // ORDERS
    CART_EMPTY(400, HttpStatus.BAD_REQUEST, "Cart is empty"),
    ORDER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Order not found"),

    // GENERAL
    VALIDATION_ERROR(400, HttpStatus.BAD_REQUEST, "Validation error"),
    METHOD_NOT_ALLOWED(405, HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    ILLEGAL_OPERATION(403, HttpStatus.FORBIDDEN, "Illegal operation"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    FILE_COMPRESSION_ERROR(500, HttpStatus.SERVICE_UNAVAILABLE, "File compression error"),
    EMAIL_SERVICE_UNAVAILABLE(503, HttpStatus.SERVICE_UNAVAILABLE, "Email service unavailable");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    BusinessErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}