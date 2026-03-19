package com.stefano.pedidos.config.filter;

import com.stefano.pedidos.util.CustomerInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.stefano.pedidos.util.PedidoContantes.*;

/**
 * Filtro para adicionar informações de contexto ao MDC (Mapped Diagnostic Context) do Logback.
 * Adiciona identificadores únicos de requisição, usuário e outros dados relevantes para logging.
 */
@Component
public class LoggingContextFilter extends OncePerRequestFilter {

    private final CustomerInfo customerInfo;

    public LoggingContextFilter(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = customerInfo.obterCorrelationId();
        if (correlationId != null) {
            MDC.put(CORRELATION_ID_MDC, correlationId);
        } else {
            // Gerar ID único para a requisição se não houver um Correlation ID fornecido
            MDC.put(REQUEST_ID_MDC, UUID.randomUUID().toString());
        }

        String sessionId = customerInfo.obterSessionId();
        if (sessionId != null) {
            MDC.put(SESSION_ID_MDC, sessionId);
        }

        Long usuarioAutenticado = customerInfo.obterUsuariIdOuNulo();
        if (usuarioAutenticado != null) {
            MDC.put(PERSON_ID_MDC, usuarioAutenticado.toString());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpar MDC após a requisição
            MDC.remove(CORRELATION_ID_MDC);
            MDC.remove(REQUEST_ID_MDC);
            MDC.remove(SESSION_ID_MDC);
            MDC.remove(PERSON_ID_MDC);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Excluir endpoints de health check e métricas do MDC para evitar poluição de logs
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") || path.startsWith("/actuator/prometheus");
    }
}
