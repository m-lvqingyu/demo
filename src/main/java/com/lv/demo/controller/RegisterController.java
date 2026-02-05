package com.lv.demo.controller;

import com.lv.demo.annotation.PhoneValid;
import com.lv.demo.annotation.UserNameValid;
import com.lv.demo.biz.RegisterBiz;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.req.register.AccountRgReq;
import com.lv.demo.req.register.PhoneRgReq;
import com.lv.demo.req.register.SmsSendReq;
import com.lv.demo.resp.ApiResponse;
import com.lv.demo.resp.sms.ImageCircleCaptchaResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author lv
 */
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterBiz registerBiz;

    @PostMapping("name/exist")
    public ApiResponse<?> checkName(@UserNameValid @RequestParam("name") String name) {
        return registerBiz.checkNameExist(name) ?
                ApiResponse.success() : ApiResponse.error(ErrorCode.USERNAME_EXIST);
    }

    @PostMapping("phone/exist")
    public ApiResponse<?> checkPhone(@PhoneValid @RequestParam("phone") String phone) {
        return registerBiz.checkPhoneExist(phone) ?
                ApiResponse.success() : ApiResponse.error(ErrorCode.PHONE_EXIST);
    }

    @PostMapping("account")
    public ApiResponse<?> accountRg(@Valid @RequestBody AccountRgReq req) {
        registerBiz.accountRegister(req);
        return ApiResponse.success();
    }

    @GetMapping("image/captcha")
    public ApiResponse<ImageCircleCaptchaResp> imageCaptcha() {
        ImageCircleCaptchaResp imageCircleCaptcha = registerBiz.getImageCircleCaptcha();
        return ApiResponse.success(imageCircleCaptcha);
    }

    @PostMapping("send/sms")
    public ApiResponse<?> sendSms(@Valid @RequestBody SmsSendReq req) {
        registerBiz.registerSmsSend(req.getPhone(), req.getCaptchaId(), req.getCaptchaCode());
        return ApiResponse.success();
    }

    @PostMapping("phone")
    public ApiResponse<?> phoneRg(@RequestBody PhoneRgReq req) {
        registerBiz.phoneRegister(req);
        return ApiResponse.success();
    }

    @GetMapping("wechat/{code}")
    public ApiResponse<?> wechatRg(@Length(min = 15, max = 32, message = "code长度不合法")
                                   @PathVariable("code") String code) {
        registerBiz.wechatRegister(code);
        return ApiResponse.success();
    }

}
