package com.stefano.pedidos.testeIntegrados.steps;

import com.stefano.pedidos.endpoints.usuarios.dto.request.UsuarioRequest;
import com.stefano.pedidos.endpoints.usuarios.service.UsuarioService;
import com.stefano.pedidos.testeIntegrados.config.CucumberSpringConfig;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class LoginSteps extends CucumberSpringConfig {

    private final UsuarioService usuarioService;
    protected final TestContext context;

    private Map<String, Object> loginRequest;
    private Map<String, Object> refrashTokenRequest;

    public LoginSteps(UsuarioService usuarioService, TestContext context) {
        this.usuarioService = usuarioService;
        this.context = context;
    }

    @Dado("que existe um usuário com nome {string}, email {string} e senha {string}")
    public void Usuario(String nome, String email, String senha) {
        if (usuarioService.obterPorEmail(email).isEmpty()) {
            usuarioService.criar(new UsuarioRequest(nome, email, senha, senha));
        }
        this.loginValido(email, senha);
    }

    @Dado("que possuo um login e email {string} e senha {string}")
    public void loginValido(String email, String senha) {
        loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("senha", senha);
    }

    @Quando("envio uma requisição para realizar login")
    public void envioRequisicaoLogin() {
        Response response = given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/auth/login");

        context.setResponse(response);

        if (response.statusCode() == 200) {
            String accessToken = context.getResponse().then().log().all()
                    .extract()
                    .path("accessToken");
            context.setAccessToken(accessToken);

            String refreshToken = context.getResponse().then().log().all()
                    .extract()
                    .path("refreshToken");
            context.setRefreshToken(refreshToken);
        }
    }

    @Então("o login deve ser realizado com sucesso")
    public void validarRetornoLogin() {
        context.getResponse().then().log().all()
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Quando("envio uma requisição para realizar refresh")
    public void envioRefreshRequisicao() {
        Response response = given().log().all()
                .contentType(ContentType.JSON)
                .body(refrashTokenRequest)
                .when()
                .post("/auth/refresh");

        context.setResponse(response);
    }

    @Dado("que realizo login e obtenho um refresh token")
    public void loginEObterRefreshToken() {
        envioRequisicaoLogin();
        refrashTokenRequest = new HashMap<>();
        refrashTokenRequest.put("refreshToken", this.context.getRefreshToken());
    }
}