package com.lv.demo.listener;

import cn.hutool.json.JSONUtil;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;

/**
 * @author lv
 */
@Slf4j
public class LoginCouponListener implements TransactionListener {

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[LoginCouponListener]-executeLocalTransaction o:{} body:{}",
                    JSONUtil.toJsonStr(o),
                    body);
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            log.error("[LoginCouponListener]-executeLocalTransaction o:{} msg:{}",
                    JSONUtil.toJsonStr(o),
                    Throwables.getStackTraceAsString(e));
            return LocalTransactionState.UNKNOW;
        }

    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            log.info("[LoginCouponListener]-checkLocalTransaction body:{}",
                    body);
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            log.error("[LoginCouponListener]-checkLocalTransaction msg:{}",
                    Throwables.getStackTraceAsString(e));
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }
    
}
