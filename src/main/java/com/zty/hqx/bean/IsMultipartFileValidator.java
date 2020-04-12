package com.zty.hqx.bean;


import com.zty.hqx.annotation.IsMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMultipartFileValidator implements ConstraintValidator<IsMultipartFile, MultipartFile> {
    @Override
    public void initialize(IsMultipartFile constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) {
            return false;
        }
        String name = file.getOriginalFilename();
        return name != null && !name.trim().isEmpty();
    }

}
