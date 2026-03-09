package com.stefano.pedidos.endpoints.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
