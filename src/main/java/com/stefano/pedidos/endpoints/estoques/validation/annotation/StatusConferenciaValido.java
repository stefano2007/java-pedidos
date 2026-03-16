package com.stefano.pedidos.endpoints.estoques.validation.annotation;

import com.stefano.pedidos.endpoints.estoques.validation.validator.StatusConferenciaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StatusConferenciaValidator.class)
@Documented
public @interface StatusConferenciaValido {

    String message() default "Status deve ser CONFERIDO ou REJEITADO";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}