package com.lv.demo.resp.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsResp {

    private Integer success;

    private String code;

    private String message;

}
