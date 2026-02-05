package com.lv.demo.utils;

import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.google.common.base.Throwables;
import com.lv.demo.enums.SendSmsStatus;
import com.lv.demo.resp.sms.SendSmsResp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
@Slf4j
@AllArgsConstructor
@Component
public class AliSmsUtil {

    private final com.aliyun.dysmsapi20170525.Client smsClient;

    public SendSmsResp doSend(String phone, String signName, String code, String param) {
        com.aliyun.dysmsapi20170525.models.SendSmsRequest request =
                new com.aliyun.dysmsapi20170525.models.SendSmsRequest().setPhoneNumbers(phone);
        request.setSignName(signName);
        request.setTemplateCode(code);
        request.setTemplateParam(param);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        SendSmsResp resp = new SendSmsResp();
        resp.setSuccess(SendSmsStatus.FAILED.getKey());
        try {
            SendSmsResponse response = smsClient.sendSmsWithOptions(request, runtime);
            log.info("[Ali]-send sms result:{}", JSONUtil.toJsonStr(response));
            Integer statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK.value()) {
                return resp;
            }
            SendSmsResponseBody body = response.getBody();
            if (!HttpStatus.OK.name().equalsIgnoreCase(body.getCode())) {
                setFailMsg(body.getMessage(), resp);
                resp.setCode(body.getCode());
                return resp;
            }
            resp.setSuccess(SendSmsStatus.SUCCESS.getKey());
            return resp;
        } catch (Exception error) {
            setFailMsg(error.getMessage(), resp);
            log.error("[Ali]-send sms error:{}", Throwables.getStackTraceAsString(error));
        }
        return resp;
    }

    private void setFailMsg(String message, SendSmsResp resp) {
        message = StrUtil.truncateByCharCount(message, 256);
        resp.setMessage(message);
    }


}
