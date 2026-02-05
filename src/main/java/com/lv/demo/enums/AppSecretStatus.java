package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum AppSecretStatus {
    /**
     * 无效
     */
    INVALID(1),

    /**
     * 有效
     */
    EFFECTIVE(5);

    private final Integer key;

    AppSecretStatus(Integer key) {
        this.key = key;
    }
    
}
