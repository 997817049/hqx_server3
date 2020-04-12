package com.zty.hqx.bean;

import com.zty.hqx.annotation.IsFeildName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsFeildNameValidator implements ConstraintValidator<IsFeildName, String> {
    @Override
    public void initialize(IsFeildName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String part, ConstraintValidatorContext constraintValidatorContext) {
        if (part == null || part.trim().isEmpty()) return false;
        return part.equals("title") || part.equals("character") || part.equals("synopsis")
                || part.equals("picUrl") || part.equals("detailUrl")
                || part.equals("type") || part.equals("time");
    }

}
