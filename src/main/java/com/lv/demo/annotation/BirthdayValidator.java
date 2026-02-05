package com.lv.demo.annotation;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lv
 */
public class BirthdayValidator implements ConstraintValidator<BirthdayValid, String> {
    
    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        try {
            DateUtil.parse(date, DatePattern.NORM_DATE_PATTERN);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
