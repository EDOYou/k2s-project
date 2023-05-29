package com.edoyou.k2sbeauty.annotation;

import com.edoyou.k2sbeauty.validation.AtLeastOneDayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * The {@code @AtLeastOneDay} annotation is a custom validation constraint that
 * ensures at least one day is specified for the object, parameter, or field it's applied to.
 *
 * <p>It uses the {@link AtLeastOneDayValidator} class to perform the validation.</p>
 *
 * <p>This annotation is retained at runtime.</p>
 *
 * <h3>Example usage:</h3>
 * <pre>
 * {@code
 *  @AtLeastOneDay
 *  public class Appointment {
 *    // fields, constructors, methods...
 *  }
 * }
 * </pre>
 *
 * <p>If the constraint is violated, a message "At least one day must be specified" will be returned.</p>
 *
 * @see AtLeastOneDayValidator
 *
 * @author Taghiyev Kanan
 * @since 2023-05-28
 */
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneDayValidator.class)
@Documented
public @interface AtLeastOneDay {

  String message() default "At least one day must be specified";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}