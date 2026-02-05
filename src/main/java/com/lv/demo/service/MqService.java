package com.lv.demo.service;

/**
 * @author lv
 */
public interface MqService {

    /**
     * 发送注册用户消息的方法
     *
     * @param userId 用户ID
     */
    void sendUserRegisterMsg(Long userId);

    /**
     * 处理用户登录优惠券消息的方法
     *
     * @param userId 用户ID
     */
    void loginCouponMsg(Long userId);

}
