package com.lv.demo.enums;

import lombok.Getter;

/**
 * @author 18891
 */
@Getter
public enum ErrorCode {

    // ===================== 基础HTTP状态码 =====================
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "禁止访问，无权限"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // ===================== 用户相关错误 (1000-1999) =====================
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USER_INACTIVE(1003, "用户未激活"),
    USER_STATUS_ABNORMAL(1004, "用户状态异常"),
    USER_PASSWORD_ERROR(1005, "用户名或密码错误"),
    USERNAME_EXIST(1006, "用户名已存在"),
    PHONE_EXIST(1007, "手机号已存在"),
    EMAIL_EXIST(1008, "邮箱已存在"),
    USER_LOCKED(1009, "用户已被锁定，请稍后重试"),
    LOGIN_CONFLICT(1010, "登录冲突，已有其他设备登录"),
    PASSWORD_EXPIRED(1011, "密码已过期，请重置密码"),
    PASSWORD_SIMPLE(1012, "密码过于简单，不符合安全要求"),
    PASSWORD_NOT_MATCH(1013, "两次输入的密码不一致"),
    USER_NOT_LOGIN(1014, "用户未登录"),

    // ===================== 令牌相关错误 (1100-1199) =====================
    INVALID_TOKEN(1101, "无效的令牌"),
    TOKEN_EXPIRED(1102, "令牌已过期"),
    TOKEN_PARSE_ERROR(1103, "令牌解析失败"),
    TOKEN_MISSING(1104, "令牌缺失"),
    APP_SECRET_INVALID(1105, "无效的APP_SECRET"),

    // ===================== 验证码/短信相关错误 (2000-2999) =====================
    CAPTCHA_NOT_EXIST(2001, "验证码不存在或已失效"),
    CAPTCHA_ERROR(2002, "验证码不正确"),
    SMS_PHONE_FORMAT_ERROR(2003, "手机号码格式不正确"),
    SMS_TEMPLATE_NOT_FOUND(2004, "短信模板不存在"),
    SMS_DAY_LIMIT(2005, "今日短信发送次数已达上限"),
    SMS_FREQUENCY_LIMIT(2006, "短信发送间隔过短，请稍后重试"),
    SMS_SEND_FAILED(2007, "短信发送失败"),
    EMAIL_SEND_FAILED(2008, "邮件发送失败"),
    CAPTCHA_FREQUENCY_LIMIT(2009, "验证码获取过于频繁，请稍后重试"),

    // ===================== 参数校验相关错误 (3000-3999) =====================
    PARAM_NULL(3001, "必填参数为空"),
    PARAM_FORMAT_ERROR(3002, "参数格式错误"),
    PARAM_RANGE_ERROR(3003, "参数值超出合法范围"),
    PARAM_TYPE_ERROR(3004, "参数类型错误"),
    DATA_DUPLICATION(3005, "数据重复，无法重复提交"),
    DATA_NOT_EXIST(3006, "数据不存在"),
    DATA_STATUS_ERROR(3007, "数据状态异常，无法操作"),

    // ===================== 限流/频率控制相关错误 (4000-4999) =====================
    FREQUENT_OPERATION(4001, "操作频繁，请稍后重试"),
    API_LIMIT_EXCEEDED(4002, "API调用次数已达上限"),
    IP_LIMIT_EXCEEDED(4003, "IP访问频率受限"),
    ACCOUNT_LIMIT_EXCEEDED(4004, "账号操作频率受限"),

    // ===================== 订单相关错误 (5000-5999) =====================
    ORDER_NOT_EXIST(5001, "订单不存在"),
    ORDER_STATUS_ERROR(5002, "订单状态异常，无法操作"),
    ORDER_CREATE_FAILED(5003, "订单创建失败"),
    ORDER_CANCEL_FAILED(5004, "订单取消失败"),
    ORDER_PAY_TIMEOUT(5005, "订单支付超时"),
    ORDER_ITEM_NOT_EXIST(5006, "订单项不存在"),
    ORDER_STOCK_INSUFFICIENT(5007, "商品库存不足"),
    ORDER_PRICE_ERROR(5008, "订单价格异常"),

    // ===================== 支付相关错误 (6000-6999) =====================
    PAYMENT_FAILED(6001, "支付失败"),
    PAYMENT_AMOUNT_ERROR(6002, "支付金额错误"),
    PAYMENT_METHOD_NOT_SUPPORT(6003, "不支持该支付方式"),
    PAYMENT_STATUS_ERROR(6004, "支付状态异常"),
    BALANCE_INSUFFICIENT(6005, "账户余额不足"),
    REFUND_FAILED(6006, "退款失败"),
    REFUND_AMOUNT_ERROR(6007, "退款金额错误"),
    REFUND_TIME_LIMIT(6008, "超出退款时限，无法退款"),

    // ===================== 文件相关错误 (7000-7999) =====================
    FILE_UPLOAD_FAILED(7001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(7002, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(7003, "不支持的文件类型"),
    FILE_NOT_EXIST(7004, "文件不存在"),
    FILE_DOWNLOAD_FAILED(7005, "文件下载失败"),
    FILE_DELETE_FAILED(7006, "文件删除失败"),

    // ===================== 权限相关错误 (8000-8999) =====================
    PERMISSION_DENIED(8001, "无此操作权限"),
    ROLE_NOT_EXIST(8002, "角色不存在"),
    ROLE_ASSIGN_FAILED(8003, "角色分配失败"),
    MENU_NOT_EXIST(8004, "菜单不存在"),

    // ===================== 第三方服务相关错误 (9000-9999) =====================
    THIRD_PARTY_SERVICE_ERROR(9001, "第三方服务调用失败"),
    WECHAT_API_ERROR(9002, "微信接口调用失败"),
    ALIPAY_API_ERROR(9003, "支付宝接口调用失败"),
    SMS_SERVICE_ERROR(9004, "短信服务调用失败"),
    OAUTH_LOGIN_FAILED(9005, "第三方登录失败"),
    REDIS_OPERATE_ERROR(9006, "Redis操作失败"),
    DB_OPERATE_ERROR(9007, "数据库操作失败");

    private final Integer code;

    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
