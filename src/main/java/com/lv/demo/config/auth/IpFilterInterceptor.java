package com.lv.demo.config.auth;

import cn.hutool.json.JSONUtil;
import com.lv.demo.config.auth.props.IpFilterProperties;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.resp.ApiResponse;
import com.lv.demo.utils.IpUtils;
import com.lv.demo.utils.SymbolConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @author lv
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class IpFilterInterceptor implements HandlerInterceptor {

    private final IpFilterProperties ipFilterProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // 如果未开启过滤，直接放行
        if (!ipFilterProperties.isEnabled()) {
            return true;
        }
        String ipAddress = getClientIp(request);
        // 1. 优先校验白名单
        if (ipFilterProperties.getWhiteList() != null && !ipFilterProperties.getWhiteList().isEmpty()) {
            if (IpUtils.ipMatches(ipAddress, ipFilterProperties.getWhiteList())) {
                return true;
            }
            log.warn("[IpFilterInterceptor]-IP {} 不在白名单中，拒绝访问", ipAddress);
            denyAccess(response);
            return false;
        }
        // 2. 校验黑名单
        if (ipFilterProperties.getBlackList() != null && !ipFilterProperties.getBlackList().isEmpty()) {
            if (IpUtils.ipMatches(ipAddress, ipFilterProperties.getBlackList())) {
                log.warn("[IpFilterInterceptor]-IP {} 在黑名单中，拒绝访问", ipAddress);
                denyAccess(response);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取客户端真实 IP
     * 考虑了反向代理（Nginx等）的情况
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || SymbolConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || SymbolConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || SymbolConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || SymbolConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况，X-Forwarded-For 通常包含多个 IP，取第一个
        if (ip != null && ip.contains(SymbolConstants.COMMA)) {
            ip = ip.split(SymbolConstants.COMMA)[0].trim();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 拒绝访问处理
     */
    private void denyAccess(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(ApiResponse.error(ErrorCode.USER_LOCKED)));
    }
}
