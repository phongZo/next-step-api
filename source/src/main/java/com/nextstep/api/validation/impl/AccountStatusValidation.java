package com.nextstep.api.validation.impl;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.AccountStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AccountStatusValidation implements ConstraintValidator<AccountStatus, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = List.of(
            NextStepConstant.STATUS_ACTIVE,
            NextStepConstant.STATUS_PENDING,
            NextStepConstant.STATUS_LOCK,
            NextStepConstant.STATUS_DELETE
    );
    @Override
    public void initialize(AccountStatus constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(kind);
    }
}
