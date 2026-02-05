package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum SmsTemplateStatus {

    /**
     * 无效
     */
    INVALID(1),

    /**
     * 有效
     */
    EFFECTIVE(5);

    private final Integer key;

    SmsTemplateStatus(Integer key) {
        this.key = key;
    }
}
