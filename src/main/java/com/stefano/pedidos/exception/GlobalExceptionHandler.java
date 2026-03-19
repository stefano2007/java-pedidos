package com.stefano.pedidos.exception;

import com.stefano.pedidos.endpoints.usuarios.exception.SenhaInvalidaException;
import com.stefano.pedidos.endpoints.usuarios.exception.UsuarioJaExisteException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String erro = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        ErroResponse erroResponse = new ErroResponse(
                LocalDateTime.now(),
                400,
                erro,
                request.getRequestURI()
        );

        logger.warn("Erro de validação: {}", erro);
        return ResponseEntity.badRequest()
                .body(erroResponse);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNotFound(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {

        logger.info("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        404,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ErroResponse> handleSenhaInvalida(
            SenhaInvalidaException ex,
            HttpServletRequest request) {
        logger.warn("Senha inválida: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<ErroResponse> handleUsuarioJaExiste(
            UsuarioJaExisteException ex,
            HttpServletRequest request) {

        logger.warn("Usuário já existe: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        409,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request) {

        logger.warn("Estado ilegal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }
}