package com.lv.demo.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lv
 */
@Component
public class SignUtil {
    /**
     * 5分钟有效期
     */
    public static final long TIMESTAMP_EXPIRE = 60 * 1000;

    public static final String APP_ID_NAME = "appId";

    public static final String SIGN_NAME = "sign";

    public static final String TIMESTAMP_NAME = "timestamp";

    public static final String KEY_NAME = "key";


    /**
     * 参数拼接生成MD5签名
     *
     * @param params    参数 k-v
     * @param appSecret 签名
     * @return MD5加密字符串
     */
    public static String generateSign(Map<String, Object> params, String appSecret) {
        // 1. 参数排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        // 2. 拼接参数
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            // 跳过签名和内部参数
            if (SIGN_NAME.equals(key) || key.startsWith(SymbolConstants.MINUS)) {
                continue;
            }
            Object value = params.get(key);
            if (value != null && !value.toString().trim().isEmpty()) {
                sb.append(key)
                        .append(SymbolConstants.EQUALS)
                        .append(value)
                        .append(SymbolConstants.AMPERSAND);
            }
        }
        // 3. 拼接密钥
        sb.append(KEY_NAME).append(SymbolConstants.EQUALS).append(appSecret);
        // 4. MD5加密
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

    /**
     * 验证签名
     */
    public static boolean verifySign(Map<String, Object> params, String appSecret) {
        String sign = (String) params.get(SIGN_NAME);
        if (StrUtil.isBlank(sign)) {
            return false;
        }
        // 验证时间戳
        Long timestamp = (Long) params.get(TIMESTAMP_NAME);
        if (timestamp == null || System.currentTimeMillis() - timestamp > TIMESTAMP_EXPIRE) {
            return false;
        }
        // 生成签名对比
        String generatedSign = generateSign(params, appSecret);
        return sign.equals(generatedSign);
    }
    
}
