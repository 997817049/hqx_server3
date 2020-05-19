package com.zty.hqx.validator;

import com.zty.hqx.classify.EModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsModelValidator implements ConstraintValidator<IsModel, String> {
    @Override
    public void initialize(IsModel constraintAnnotation) {

    }

    @Override
    public boolean isValid(String part, ConstraintValidatorContext constraintValidatorContext) {
        if (part == null || part.trim().isEmpty()) return false;
        for(EModel model : EModel.values()){
            String english = model.getEnglish();
            if(part.equals(english)){
                return true;
            }
        }
        return false;
    }

}
