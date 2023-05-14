package com.edoyou.k2sbeauty.annotation;

import com.edoyou.k2sbeauty.validation.AtLeastOneDayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneDayValidator.class)
@Documented
public @interface AtLeastOneDay {

  String message() default "At least one day must be specified";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}