package com.zty.hqx.annotation;

import com.zty.hqx.bean.IsPartNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsPartNameValidator.class})
public @interface IsPartName {
    String message() default "文件错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
