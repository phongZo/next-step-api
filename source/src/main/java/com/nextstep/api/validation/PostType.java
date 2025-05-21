package com.nextstep.api.validation;

import com.nextstep.api.validation.impl.PostTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostTypeValidation.class)
@Documented
public @interface PostType {
    boolean allowNull() default false;
    String message() default "Post type invalid. Valid values is 0: home, 1: office, 2: remote, 3: hybrid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
