package com.nextstep.api.validation;

import com.nextstep.api.validation.impl.JobApplicationStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobApplicationStateValidation.class)
@Documented
public @interface JobApplicationState {
    boolean allowNull() default false;
    String message() default "Job application state invalid. Valid values is 0 (pending), 1 (approved), or 2 (cancelled)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 