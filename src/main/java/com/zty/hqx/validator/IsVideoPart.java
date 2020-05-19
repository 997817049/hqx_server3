package com.zty.hqx.validator;

import com.zty.hqx.validator.IsVideoPartValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsVideoPartValidator.class})
public @interface IsVideoPart {
    String message() default "videoPart错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
