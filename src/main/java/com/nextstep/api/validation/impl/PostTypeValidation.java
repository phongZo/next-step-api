package com.nextstep.api.validation.impl;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.PostType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PostTypeValidation implements ConstraintValidator<PostType, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = Arrays.asList(
            NextStepConstant.POST_TYPE_HOME,
            NextStepConstant.POST_TYPE_OFFICE,
            NextStepConstant.POST_TYPE_REMOTE,
            NextStepConstant.POST_TYPE_HYBRID
    );

    @Override
    public void initialize(PostType constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(value);
    }
}
