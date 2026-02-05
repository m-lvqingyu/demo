package com.lv.demo.controller;

import com.lv.demo.biz.UserBiz;
import com.lv.demo.pojo.UserInfo;
import com.lv.demo.resp.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lv
 */
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/user")
@RestController
public class UserInfoController {

    private final UserBiz userBiz;

    @GetMapping("info/{id}")
    public ApiResponse<UserInfo> getUserInfo(@PathVariable("id") Long userId) {
        return ApiResponse.success(userBiz.getUserById(userId));
    }
    
}
