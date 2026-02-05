package com.lv.demo.req.register;

import com.lv.demo.annotation.PhoneValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @author lv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneRgReq {

    @PhoneValid
    private String phone;

    @Length(min = 20, max = 33, message = "验证码ID不正确")
    private String captchaId;

    @Length(min = 4, max = 6, message = "验证码不正确")
    private String code;

}
