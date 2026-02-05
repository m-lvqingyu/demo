package com.lv.demo.exception;

import com.lv.demo.enums.ErrorCode;
import lombok.Getter;

/**
 * @author lv
 */
@Getter
public class BaseException extends RuntimeException {
    
    private final Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

}
