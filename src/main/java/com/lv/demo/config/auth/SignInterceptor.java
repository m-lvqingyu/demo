package com.lv.demo.config.auth;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.lv.demo.annotation.VerifySign;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.resp.ApiResponse;
import com.lv.demo.service.AppSecretService;
import com.lv.demo.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lv
 */
@Component
public class SignInterceptor implements HandlerInterceptor {

    private final AppSecretService appSecretService;

    public SignInterceptor(AppSecretService appSecretService) {
        this.appSecretService = appSecretService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // 检查是否需要验证签名
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        VerifySign verifySign = handlerMethod.getMethodAnnotation(VerifySign.class);
        if (verifySign == null || !verifySign.required()) {
            return true;
        }
        // 获取请求参数
        Map<String, Object> params = new HashMap<>();
        // 1. 获取GET参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            params.put(entry.getKey(), values != null && values.length > 0 ? values[0] : "");
        }
        // 2. 获取POST JSON参数
        if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) &&
                request.getContentType() != null &&
                request.getContentType().contains(ContentType.JSON.getValue())) {
            // 读取请求体
            String body = getRequestBody(request);
            if (StrUtil.isNotEmpty(body)) {
                Map<String, Object> bodyParams = JSONUtil.parseObj(body);
                params.putAll(bodyParams);
            }
        }
        // 验证必要参数
        if (!params.containsKey(SignUtil.APP_ID_NAME) ||
                !params.containsKey(SignUtil.TIMESTAMP_NAME) ||
                !params.containsKey(SignUtil.SIGN_NAME)) {
            responseError(response, ErrorCode.BAD_REQUEST);
            return false;
        }
        // 根据appId获取密钥
        String appId = params.get(SignUtil.APP_ID_NAME).toString();
        String appSecret = getAppSecret(appId);
        if (appSecret == null) {
            responseError(response, ErrorCode.BAD_REQUEST);
            return false;
        }
        // 验证签名
        if (!SignUtil.verifySign(params, appSecret)) {
            responseError(response, ErrorCode.INVALID_TOKEN);
            return false;
        }
        return true;
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            return request.getReader().lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            return "";
        }
    }

    private String getAppSecret(String appId) {
        return appSecretService.getByCache(appId);
    }

    private void responseError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType(ContentType.JSON.getValue());
        response.setStatus(ErrorCode.SUCCESS.getCode());
        response.getWriter().write(JSONUtil.toJsonStr(ApiResponse.error(errorCode.getCode(), errorCode.getMessage())));
    }

}
