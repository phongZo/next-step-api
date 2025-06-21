package com.nextstep.api.validation.impl;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.CategoryKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class CategoryKindValidation implements ConstraintValidator<CategoryKind, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_KIND_VALUES = List.of(
            NextStepConstant.CATEGORY_KIND_NEWS,
            NextStepConstant.CATEGORY_KIND_SKILL,
            NextStepConstant.CATEGORY_KIND_JOB,
            NextStepConstant.CATEGORY_KIND_LEVEL,
            NextStepConstant.CATEGORY_KIND_EDUCATION,
            NextStepConstant.CATEGORY_KIND_SPECIALIZATION
    );

    @Override
    public void initialize(CategoryKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null && allowNull) {
            return true;
        }
        if (kind == null) {
            return false;
        }
        return VALID_KIND_VALUES.contains(kind);
    }
}
