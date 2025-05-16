package com.nextstep.api.validation.impl;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.PostContractType;
import com.nextstep.api.validation.PostType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PostContractTypeValidation implements ConstraintValidator<PostContractType, Integer> {
    private boolean allowNull;

    private static final List<Integer> VALID_VALUES = Arrays.asList(
            NextStepConstant.POST_CONTRACT_TYPE_PART_TIME,
            NextStepConstant.POST_CONTRACT_TYPE_FULL_TIME,
            NextStepConstant.POST_CONTRACT_TYPE_COLLAB
    );

    @Override
    public void initialize(PostContractType constraintAnnotation) {
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
