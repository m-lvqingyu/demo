package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum UserStatus {
    /**
     * 用户状态
     */
    DISABLED(0, "禁用"),

    INACTIVE(5, "未激活"),

    NORMAL(10, "正常");


    private final Integer code;

    private final String description;

    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
