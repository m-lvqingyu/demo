package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum DelStatus {

    /**
     * 已删除
     */
    YES(1),

    /**
     * 未删除
     */
    NO(5);

    private final Integer key;

    DelStatus(Integer key) {
        this.key = key;
    }

}
