package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author lv
 */
@Getter
public enum SmsPlatform {

    /**
     * 阿里云
     */
    ALI(1);
    
    private final int key;

    SmsPlatform(int key) {
        this.key = key;
    }
}
