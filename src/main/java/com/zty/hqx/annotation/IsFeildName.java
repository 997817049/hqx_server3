package com.zty.hqx.annotation;

import com.zty.hqx.bean.IsFeildNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsFeildNameValidator.class})
public @interface IsFeildName {
    String message() default "文件错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
