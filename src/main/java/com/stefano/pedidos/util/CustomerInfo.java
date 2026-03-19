package com.stefano.pedidos.util;

import com.stefano.pedidos.config.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stefano.pedidos.util.PedidoContantes.CORRELATION_ID_HEADER;
import static com.stefano.pedidos.util.PedidoContantes.SESSION_ID_HEADER;

@Component
public class CustomerInfo {

    private final HttpServletRequest requisicaoUsuario;

    public CustomerInfo(HttpServletRequest requisicaoUsuario) {
        this.requisicaoUsuario = requisicaoUsuario;
    }

    public String obterCorrelationId() {
        return requisicaoUsuario.getHeader(CORRELATION_ID_HEADER);
    }

    public String obterSessionId() {
        return requisicaoUsuario.getHeader(SESSION_ID_HEADER);
    }

    public Long obterUsuariIdOuNulo() {
        return this.obterUsuarioLogado()
                .map(UserPrincipal::getUsuarioId)
                .orElse(null);
    }

    public String obterUsarNameOuNulo() {
        return this.obterUsuarioLogado()
                .map(UserPrincipal::getUsername)
                .orElse(null);
    }


    private Optional<UserPrincipal> obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal);
        }

        return Optional.empty();
    }
}
