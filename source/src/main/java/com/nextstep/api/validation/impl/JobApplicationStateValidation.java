package com.nextstep.api.validation.impl;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.JobApplicationState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class JobApplicationStateValidation implements ConstraintValidator<JobApplicationState, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = List.of(
            NextStepConstant.JOB_APPLICATION_STATE_PENDING,
            NextStepConstant.JOB_APPLICATION_STATE_APPROVED,
            NextStepConstant.JOB_APPLICATION_STATE_CANCELLED
    );
    
    @Override
    public void initialize(JobApplicationState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer state, ConstraintValidatorContext constraintValidatorContext) {
        if (state == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(state);
    }
} 