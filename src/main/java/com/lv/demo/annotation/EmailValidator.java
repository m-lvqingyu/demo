package com.lv.demo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * @author 18891
 */
public class EmailValidator implements ConstraintValidator<EmailValid, String> {

    /**
     * 预编译正则表达式，提高性能。
     * 这是一个标准的、较为严格的邮箱正则表达式。
     * 逻辑：
     * 1. ^[a-zA-Z0-9_+&*-]+ : 开头部分，允许字母、数字及特定符号
     * 2. (?:\\.[a-zA-Z0-9_+&*-]+)* : 中间可以有点号分隔，例如 user.name
     * 3. (?:\\.[a-zA-Z0-9_+&*-]+) : 域名前缀
     * 4. @ : 必须包含的@符号
     * 5. (?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$ : 域名后缀，例如 example.com 或 sub.example.co.uk
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private static final int EMAIL_MAX_LENGTH = 20;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null) {
            return false;
        }
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            return false;
        }
        if (trimmedEmail.length() > EMAIL_MAX_LENGTH) {
            return false;
        }
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }

}
