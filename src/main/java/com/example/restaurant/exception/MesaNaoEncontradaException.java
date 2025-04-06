package com.example.restaurant.exception;

public class MesaNaoEncontradaException extends RuntimeException {
    public MesaNaoEncontradaException(String message) {
        super(message);
    }
} 