package com.lv.demo.listener;

import com.lv.demo.config.mq.RocketMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author 18891
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RocketMqConfig.USER_REGISTER_TOPIC,
        consumerGroup = RocketMqConfig.USER_REGISTER_GROUP,
        messageModel = MessageModel.CLUSTERING)
public class UserRegisterListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        log.info("UserRegisterListener msg:{}", s);
    }
}
