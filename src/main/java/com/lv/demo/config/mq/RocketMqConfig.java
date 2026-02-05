package com.lv.demo.config.mq;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lv.demo.listener.LoginCouponListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.StringMessageConverter;

import java.util.concurrent.*;

/**
 * @author 18891
 */
@Slf4j
@Configuration
public class RocketMqConfig {

    public static final String USER_REGISTER_TOPIC = "USER-REGISTER-TOPIC";

    public static final String USER_REGISTER_GROUP = "USER_REGISTER_GROUP";

    public static final String LOGIN_COUPON_TOPIC = "LOGIN_COUPON_TOPIC";

    public static final String LOGIN_COUPON_GROUP = "LOGIN_COUPON_GROUP";

    @Value("${rocketmq.name-server}")
    private String mqAddress;

    @Bean("userRegisterMqTemplate")
    public RocketMQTemplate userRegisterMqTemplate() {
        RocketMQTemplate template = new RocketMQTemplate();
        DefaultMQProducer producer = new DefaultMQProducer(USER_REGISTER_GROUP);
        producer.setNamesrvAddr(mqAddress);
        producer.setSendMsgTimeout(5000);
        producer.setRetryTimesWhenSendFailed(2);
        template.setProducer(producer);
        return template;
    }

    @Bean
    public LoginCouponListener loginCouponListener() {
        return new LoginCouponListener();
    }

    @Bean("loginCouponTransactionProducer")
    public TransactionMQProducer loginCouponTransactionProducer(LoginCouponListener loginCouponListener) {
        TransactionMQProducer producer = new TransactionMQProducer(LOGIN_COUPON_GROUP);
        producer.setNamesrvAddr(mqAddress);
        producer.setSendMsgTimeout(5000);
        producer.setRetryTimesWhenSendFailed(2);
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        // 设置事务监听器
        producer.setTransactionListener(loginCouponListener);
        // 线程池配置
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("login-coupon-transaction-executor-%d")
                .build();
        ExecutorService executorService = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000), threadFactory);
        producer.setExecutorService(executorService);
        return producer;
    }

    @Bean("loginCouponMqTemplate")
    public RocketMQTemplate loginCouponMqTemplate(
            @Qualifier("loginCouponTransactionProducer") TransactionMQProducer loginCouponTransactionProducer) {
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(loginCouponTransactionProducer);
        // 配置消息转换器
        template.setMessageConverter(new StringMessageConverter());
        return template;
    }

}
