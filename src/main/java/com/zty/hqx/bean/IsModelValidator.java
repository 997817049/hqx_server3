package com.zty.hqx.bean;

import com.zty.hqx.annotation.IsModel;
import com.zty.hqx.annotation.IsStudyPart;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;

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
            String english = model.toString().toLowerCase();
            if(part.equals(english)){
                return true;
            }
        }
        return false;
    }

}
