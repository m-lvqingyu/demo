package com.lv.demo.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
//@Component
public class SentinelExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       String s,
                       BlockException e) throws Exception {
        httpServletResponse.setStatus(429);
        httpServletResponse.setContentType("application/json;charset=utf-8");

        String message = switch (e) {
            case FlowException flowException -> "限流了";
            case DegradeException degradeException -> "降级了";
            case ParamFlowException paramFlowException -> "热点参数限流";
            case SystemBlockException systemBlockException -> "系统规则限流";
            case null, default -> "未知限流";
        };
        httpServletResponse.getWriter().write("{\"code\":429,\"msg\":\"" + message + "\"}");
    }
}
