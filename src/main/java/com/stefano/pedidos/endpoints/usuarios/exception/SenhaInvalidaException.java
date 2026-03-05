package com.stefano.pedidos.endpoints.usuarios.exception;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException(String message) {
        super(message);
    }
}