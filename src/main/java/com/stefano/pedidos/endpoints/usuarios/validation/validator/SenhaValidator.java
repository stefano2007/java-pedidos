package com.stefano.pedidos.endpoints.usuarios.validation.validator;

import com.stefano.pedidos.endpoints.usuarios.validation.annotation.SenhaValida;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SenhaValidator implements ConstraintValidator<SenhaValida, String> {
    private static final String SENHA_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern PATTERN = Pattern.compile(SENHA_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }
}

