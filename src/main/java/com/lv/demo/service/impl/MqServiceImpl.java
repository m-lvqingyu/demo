package com.lv.demo.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Throwables;
import com.lv.demo.config.mq.RocketMqConfig;
import com.lv.demo.service.MqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 18891
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MqServiceImpl implements MqService {

    private final RocketMQTemplate userRegisterMqTemplate;

    private final RocketMQTemplate loginCouponMqTemplate;

    @Override
    public void sendUserRegisterMsg(Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        String msg = JSONUtil.toJsonStr(map);
        userRegisterMqTemplate.asyncSend(RocketMqConfig.USER_REGISTER_TOPIC,
                MessageBuilder.withPayload(msg).build(),
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("[SendUserRegisterMsg]-success! msg:{} result:{}",
                                msg,
                                JSONUtil.toJsonStr(sendResult));
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("[SendUserRegisterMsg]-fail! msg:{} result:{}",
                                msg,
                                Throwables.getStackTraceAsString(throwable));
                    }
                }
        );
    }

    @Override
    public void loginCouponMsg(Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        String msg = JSONUtil.toJsonStr(map);
        Message<String> message = MessageBuilder.withPayload(msg).build();
        Map<String, String> bizParam = new HashMap<>();
        bizParam.put("bizId", IdUtil.getSnowflakeNextIdStr());
        String arg = JSONUtil.toJsonStr(bizParam);
        TransactionSendResult sendResult = loginCouponMqTemplate.sendMessageInTransaction(
                RocketMqConfig.LOGIN_COUPON_TOPIC,
                message,
                arg);
        log.info("[LoginCouponMsg]-success! msg:{} arg:{} result:{}", msg, arg, JSONUtil.toJsonStr(sendResult));
    }
}
