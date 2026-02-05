package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */

@Getter
public enum UserVerified {
    /**
     * 用户验证状态
     */
    NO(0, "未验证"),

    YEX(1, "已验证");

    private final Integer code;

    private final String description;

    UserVerified(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
