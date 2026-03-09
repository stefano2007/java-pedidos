package com.stefano.pedidos.endpoints.auth.dto.request;

public record LoginRequest(
        String email,
        String senha
) {}