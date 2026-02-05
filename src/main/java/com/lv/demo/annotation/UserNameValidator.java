package com.lv.demo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * @author lv
 */
public class UserNameValidator implements ConstraintValidator<UserNameValid, String> {

    /**
     * 正则表达式：长度6-20，只能包含字母和数字，不能以数字开头
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{5,19}$");

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        // 1. 检查是否为空
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
}
