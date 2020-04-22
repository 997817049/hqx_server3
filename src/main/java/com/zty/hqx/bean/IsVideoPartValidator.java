package com.zty.hqx.bean;

import com.zty.hqx.annotation.IsVideoPart;
import com.zty.hqx.classify.EStudyPart;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsVideoPartValidator implements ConstraintValidator<IsVideoPart, String> {
    @Override
    public void initialize(IsVideoPart constraintAnnotation) {

    }

    @Override
    public boolean isValid(String part, ConstraintValidatorContext constraintValidatorContext) {
        if (part == null || part.trim().isEmpty()) return false;
        return part.equals(EStudyPart.TELEPLAY.getEnglish())
                || part.equals(EStudyPart.FILM.getEnglish())
                || part.equals(EStudyPart.VARIETY.getEnglish())
                || part.equals(EStudyPart.DOCUMENTARY.getEnglish())
                || part.equals(EStudyPart.DRAMA.getEnglish());
    }

}
