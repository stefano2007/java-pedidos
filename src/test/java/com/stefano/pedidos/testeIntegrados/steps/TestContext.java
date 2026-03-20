package com.stefano.pedidos.testeIntegrados.steps;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;


@Component
public class TestContext {

    private Response response;
    private String accessToken;
    private String refreshToken;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}