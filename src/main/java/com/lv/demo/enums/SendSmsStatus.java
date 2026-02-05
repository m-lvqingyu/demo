package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum SendSmsStatus {

    /**
     * 失败
     */
    FAILED(1),

    /**
     * 成功
     */
    SUCCESS(5);
    
    private final int key;

    SendSmsStatus(int key) {
        this.key = key;
    }
}
