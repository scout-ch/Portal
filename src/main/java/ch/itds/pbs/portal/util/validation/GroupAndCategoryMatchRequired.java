package ch.itds.pbs.portal.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GroupAndCategoryMatchRequiredValidator.class)
public @interface GroupAndCategoryMatchRequired {

    String message() default "allowed group is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
