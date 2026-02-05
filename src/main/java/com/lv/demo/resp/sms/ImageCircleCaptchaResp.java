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
public class ImageCircleCaptchaResp {

    private String captchaId;

    private String imageBase64Data;

}
