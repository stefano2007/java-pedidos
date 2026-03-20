package com.stefano.pedidos.testeIntegrados.steps;

import com.stefano.pedidos.testeIntegrados.config.CucumberSpringConfig;
import io.cucumber.java.pt.Então;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BaseSteps extends CucumberSpringConfig {

    @Autowired
    protected TestContext context;

    @Então("o status da resposta deve ser {int}")
    public void validarStatus(int status) {
        context.getResponse().then().statusCode(status);
    }

    @Então("o response com path {string}, status {int} e mensagem {string} devem ser retornados")
    public void retornoExcessao(String path, int status, String mensagem) {
        context.getResponse().then().log().all()
                .body("timestamp", notNullValue())
                .body("status", equalTo(status))
                .body("mensagem", equalTo(mensagem))
                .body("path", equalTo(path));
    }
}
