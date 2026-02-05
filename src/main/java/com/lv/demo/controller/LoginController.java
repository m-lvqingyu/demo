package com.lv.demo.controller;

import com.lv.demo.biz.LoginBiz;
import com.lv.demo.req.login.AccountLoginReq;
import com.lv.demo.resp.ApiResponse;
import com.lv.demo.resp.auth.TokenResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lv
 */
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/login")
@RestController
public class LoginController {

    private final LoginBiz loginBiz;

    @PostMapping("account")
    public ApiResponse<TokenResp> account(@Valid @RequestBody AccountLoginReq req) {
        TokenResp tokenResp = loginBiz.pwLogin(req.getUserName(), req.getPassword());
        return ApiResponse.success(tokenResp);
    }
}
