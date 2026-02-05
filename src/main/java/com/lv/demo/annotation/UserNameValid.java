package com.lv.demo.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author lv
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = UserNameValidator.class)
public @interface UserNameValid {

    String message() default "用户名长度在6-20个字符之间，包含：大小写字母（A-Z, a-z）和数字（0-9），且不能以数字开头";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
