package com.zty.hqx.bean;

import com.zty.hqx.annotation.IsPartName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsPartNameValidator implements ConstraintValidator<IsPartName, String> {
    @Override
    public void initialize(IsPartName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String part, ConstraintValidatorContext constraintValidatorContext) {
        if (part == null || part.trim().isEmpty()) return false;
        return part.equals("exam") || part.equals("literature")
                || part.equals("teleplay") || part.equals("film");
    }

}
