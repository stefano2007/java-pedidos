package com.stefano.pedidos.exception;

import com.stefano.pedidos.endpoints.usuarios.exception.SenhaInvalidaException;
import com.stefano.pedidos.endpoints.usuarios.exception.UsuarioJaExisteException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

        return ResponseEntity.badRequest()
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        erro,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNotFound(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {

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
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }


}