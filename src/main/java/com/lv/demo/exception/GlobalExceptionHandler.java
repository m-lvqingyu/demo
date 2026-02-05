package com.lv.demo.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.resp.ApiResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lv
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ApiResponse<?> handleValidationException(ValidationException e) {
        log.error("ValidationException message={}", e.getMessage());
        return ApiResponse.error(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> fieldErrorMap = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (!fieldErrorMap.containsKey(fieldError.getField())) {
                fieldErrorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }
        String allErrorMsg = String.join("; ", fieldErrorMap.values());
        log.error("MethodArgumentNotValidException message={}", allErrorMsg);
        return ApiResponse.error(ErrorCode.BAD_REQUEST, fieldErrorMap);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ApiResponse<Void> handleBusinessException(BaseException e) {
        log.error("BaseException: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Exception: message={}", e.getMessage());
        // 检查是否是 Sentinel 导致的 UndeclaredThrowableException
        if (e instanceof UndeclaredThrowableException undeclared) {
            Throwable cause = undeclared.getUndeclaredThrowable();
            if (cause instanceof BlockException) {
                // 重新包装为运行时异常，或者直接返回特定响应
                return handleSentinelBlock((BlockException) cause);
            }
        }
        // 直接处理 BlockException（如果未被包装）
        if (e instanceof BlockException blockException) {
            return handleSentinelBlock(blockException);
        }
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
    }

    private ApiResponse<Void> handleSentinelBlock(BlockException e) {
        String message = switch (e) {
            case FlowException flowException -> "限流了";
            case DegradeException degradeException -> "降级了";
            case ParamFlowException paramFlowException -> "热点参数限流";
            case SystemBlockException systemBlockException -> "系统规则限流";
            default -> ErrorCode.INTERNAL_ERROR.getMessage();
        };
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(), message);
    }
}
