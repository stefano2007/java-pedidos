package com.stefano.pedidos.testeIntegrados.steps;

import com.stefano.pedidos.testeIntegrados.config.CucumberSpringConfig;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UsuarioSteps extends CucumberSpringConfig {

    protected final TestContext context;

    private Map<String, Object> usuario;
    private String usuarioId;

    public UsuarioSteps(TestContext context) {
        this.context = context;
    }

    @Dado("que quero criar um usuario nome {string} com email {string} e senha {string}")
    public void usuarioValido(String nome, String email, String senha) {
        usuario = new HashMap<>();
        usuario.put("nome", nome);
        usuario.put("email", email);
        usuario.put("senha", senha);
        usuario.put("confirmacaoSenha", senha);
    }

    @Dado("que quero criar um usuario nome {string} com email {string}, senha {string} e senha de confirmação {string}")
    public void usuarioValido(String nome, String email, String senha, String confirmacaoSenha) {
        usuario = new HashMap<>();
        usuario.put("nome", nome);
        usuario.put("email", email);
        usuario.put("senha", senha);
        usuario.put("confirmacaoSenha", confirmacaoSenha);
    }

    @Quando("envio uma requisição para criar o usuário")
    public void envioRequisicao() {
        Response response = given().log().all()
                .contentType(ContentType.JSON)
                .body(usuario)
                .when()
                .post("/usuarios");

        context.setResponse(response);
    }


    @Então("o usuário deve ser criado com sucesso com nome {string} e email {string} e ativo")
    public void validarCriacao(String nome, String email) {
        context.getResponse().then().log().all()
                .body("id", notNullValue())
                .body("nome", equalTo(nome))
                .body("email", equalTo(email))
                .body("dataCriacao", notNullValue())
                .body("ativo", equalTo(true));
    }

    @Quando("envio uma requisição para obter o usuário ID {string}")
    public void obterUsuario(String usuarioId) {
        Response response = given().log().all()
                .header("Authorization", "Bearer " + context.getAccessToken())
                .when()
                .get("/usuarios/" + usuarioId);

        context.setResponse(response);
    }

    @Então("o usuário deve ser retornado")
    public void validarUsuario() {
        context.getResponse().then().log().all()
                .body("id", notNullValue())
                .body("nome", notNullValue())
                .body("email", notNullValue())
                .body("dataCriacao", notNullValue())
                .body("ativo", equalTo(true));
    }

}
