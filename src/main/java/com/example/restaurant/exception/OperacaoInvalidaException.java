package com.example.restaurant.exception;

public class OperacaoInvalidaException extends RuntimeException {
    public OperacaoInvalidaException(String message) {
        super(message);
    }
} 