package com.stefano.pedidos.endpoints.usuarios.validation.annotation;

import com.stefano.pedidos.endpoints.usuarios.validation.validator.SenhaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SenhaValidator.class)
@Documented
public @interface SenhaValida {
    String message() default "A senha deve conter no mínimo 8 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
