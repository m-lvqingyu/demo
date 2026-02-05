package com.lv.demo.annotation;

import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author lv
 */
public class PhoneValidator implements ConstraintValidator<PhoneValid, String> {

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        // 1. 检查是否为空
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PhoneUtil.isPhone(phone);
    }
}
