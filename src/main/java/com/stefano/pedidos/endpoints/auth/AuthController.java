package com.stefano.pedidos.endpoints.auth;

import com.stefano.pedidos.config.model.UserPrincipal;
import com.stefano.pedidos.endpoints.auth.dto.request.LoginRequest;
import com.stefano.pedidos.endpoints.auth.dto.request.RefreshRequest;
import com.stefano.pedidos.endpoints.auth.dto.response.AuthResponse;
import com.stefano.pedidos.endpoints.auth.service.CustomUserDetailsService;
import com.stefano.pedidos.endpoints.auth.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.stefano.pedidos.util.PedidoContantes.SESSION_ID_HEADER;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        String accessToken = jwtService.gerarAccessToken(user);
        String refreshToken = jwtService.gerarRefreshToken(user);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(SESSION_ID_HEADER, UUID.randomUUID().toString());

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .body(new AuthResponse(accessToken, refreshToken));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshRequest request) {

        String username = jwtService.extrairUsuario(request.refreshToken());

        UserPrincipal user = (UserPrincipal) customUserDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(request.refreshToken(), user)) {

            String newAccessToken = jwtService.gerarAccessToken(user);

            return ResponseEntity.ok(
                    new AuthResponse(newAccessToken, request.refreshToken())
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}