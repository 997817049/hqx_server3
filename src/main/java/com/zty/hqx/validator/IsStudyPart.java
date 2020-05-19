package com.zty.hqx.validator;

import com.zty.hqx.validator.IsStudyPartValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsStudyPartValidator.class})
public @interface IsStudyPart {
    String message() default "studyPart错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
