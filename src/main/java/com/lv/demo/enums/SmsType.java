package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum SmsType {

    /**
     * 登录
     */
    LOGIN(1);

    private final int key;

    SmsType(int key) {
        this.key = key;
    }
}
