package com.lv.demo.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author lv
 */
public class IpUtils {

    /**
     * 校验 IP 是否在规则列表中（支持通配符 *）
     *
     * @param ip       待校验的 IP
     * @param ipRules 规则列表
     * @return true: 命中规则; false: 未命中
     */
    public static boolean ipMatches(String ip, List<String> ipRules) {
        if (StringUtils.isEmpty(ip) || ipRules == null || ipRules.isEmpty()) {
            return false;
        }
        for (String rule : ipRules) {
            if (rule.equals(ip)) {
                return true;
            }
            // 处理通配符情况
            if (rule.contains("*")) {
                // 将 192.168.1.* 转换为正则表达式 192\.168\.1\..*
                String regex = rule.replace(".", "\\.");
                regex = regex.replace("*", ".*");
                if (Pattern.matches(regex, ip)) {
                    return true;
                }
            }
        }
        return false;
    }

}
