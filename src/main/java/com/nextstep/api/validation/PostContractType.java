package com.nextstep.api.validation;

import com.nextstep.api.validation.impl.PostContractTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostContractTypeValidation.class)
@Documented
public @interface PostContractType {
    boolean allowNull() default false;
    String message() default "Post Contract Type invalid. Valid values is 0: partTime, 1: fulltime, 2: collab";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
