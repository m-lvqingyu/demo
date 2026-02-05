package com.lv.demo.config.auth.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lv
 */
@Data
@Component
@ConfigurationProperties(prefix = "ip.filter")
public class IpFilterProperties {

    /**
     * 是否开启 IP 过滤
     */
    private boolean enabled = false;

    /**
     * 黑名单列表
     * 支持具体 IP (如 192.168.1.1) 和 通配符 (如 192.168.1.*)
     */
    private List<String> blackList = new ArrayList<>();

    /**
     * 白名单列表
     * 如果白名单不为空，则只允许白名单内的 IP 访问，黑名单失效
     */
    private List<String> whiteList = new ArrayList<>();
}
