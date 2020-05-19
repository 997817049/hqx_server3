package com.zty.hqx.validator;

import com.zty.hqx.classify.EStudyPart;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsStudyPartValidator implements ConstraintValidator<IsStudyPart, String> {
    @Override
    public void initialize(IsStudyPart constraintAnnotation) { }

    @Override
    public boolean isValid(String part, ConstraintValidatorContext constraintValidatorContext) {
        if (part == null || part.trim().isEmpty()) return false;
        for(EStudyPart studyPart : EStudyPart.values()){
            String english = studyPart.getEnglish();
            if(part.equals(english)){
                return true;
            }
        }
        return false;
    }

}
