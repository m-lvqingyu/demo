package com.lv.demo.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lv
 */
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IpFilterInterceptor ipFilterInterceptor;

    private final SignInterceptor signInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 先注册 IP 拦截器 -> 它会排在最前面，最先执行
        registry.addInterceptor(ipFilterInterceptor)
                .addPathPatterns("/**")
                .order(1);
        // 2. 后注册其他拦截器 -> 它会在 IP 拦截器之后执行
        registry.addInterceptor(signInterceptor)
                .addPathPatterns("/**")
                .order(2);
    }

}
