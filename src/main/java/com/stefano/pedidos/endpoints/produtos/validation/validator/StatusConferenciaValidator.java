package com.stefano.pedidos.endpoints.produtos.validation.validator;

import com.stefano.pedidos.endpoints.produtos.entity.StatusEstoque;
import com.stefano.pedidos.endpoints.produtos.validation.annotation.StatusConferenciaValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusConferenciaValidator
        implements ConstraintValidator<StatusConferenciaValido, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        try {
            StatusEstoque status = StatusEstoque.from(value);

            return status == StatusEstoque.CONFERIDO
                    || status == StatusEstoque.REJEITADO;

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
