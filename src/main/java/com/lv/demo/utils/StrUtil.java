package com.lv.demo.utils;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lv
 */
public class StrUtil {

    /**
     * 字符串占位符替换
     *
     * @param template 占位符。比如：{code}
     * @param values   替换值{"code":"3849"}
     * @return 替换后的字符串。比如：3849
     */
    public static String replace(String template, Map<String, Object> values) {
        StringSubstitutor sub = new StringSubstitutor(values);
        return sub.replace(template);
    }

    /**
     * 安全截断字符串，确保不超过最大字符数限制
     *
     * @param str      原始字符串
     * @param maxChars 最大字符数
     * @return 截断后的字符串
     */
    public static String truncateByCharCount(String str, int maxChars) {
        if (str == null || maxChars <= 0) {
            return "";
        }
        // 如果字符数未超限，直接返回
        int charCount = str.codePointCount(0, str.length());
        if (charCount <= maxChars) {
            return str;
        }
        // 逐个字符截取
        StringBuilder result = new StringBuilder();
        int currentCharCount = 0;
        int index = 0;
        while (index < str.length() && currentCharCount < maxChars) {
            int codePoint = str.codePointAt(index);
            result.appendCodePoint(codePoint);
            currentCharCount++;
            index += Character.charCount(codePoint);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "3423");
        String replace = replace("您的验证码是{${code}}", map);
        System.out.println(replace);
    }
}
