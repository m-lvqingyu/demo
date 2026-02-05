package com.lv.demo.utils;

/**
 * @author lv
 */
public class SmsConstants {

    /**
     * 用户注册发送次数限制
     */
    public static final String SMS_REGISTER_DAY_LIMIT_PREFIX = "sms:rg:day:";

    public static final int SMS_REGISTER_DAY_LIMIT = 5;

    public static final int SEND_TIMES_CACHE_HOURS = 25;

    public static final String SMS_REGISTER_LAST_LIMIT_PREFIX = "sms:rg:last:";

    public static final long SMS_REGISTER_LAST_LIMIT = 5;

    public static final String SMS_REGISTER_CODE_PREFIX = "sms:rg:code:";

    public static final String SMS_REGISTER_IMAGE_CIRCLE_CAPTCHA_PREFIX = "sms:rg:im:cic:";


    /**
     * Redis key 分隔符
     */
    public static final String KEY_SEPARATOR = ":";

}
