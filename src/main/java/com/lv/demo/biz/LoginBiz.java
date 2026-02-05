package com.lv.demo.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lv.demo.enums.DelStatus;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.enums.UserStatus;
import com.lv.demo.exception.BaseException;
import com.lv.demo.pojo.UserInfo;
import com.lv.demo.resp.auth.TokenResp;
import com.lv.demo.service.MqService;
import com.lv.demo.service.UserInfoService;
import com.lv.demo.utils.JwtUtil;
import com.lv.demo.utils.PasswordUtil;
import com.lv.demo.utils.WeChatAuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lv
 */
@RequiredArgsConstructor
@Component
public class LoginBiz {

    private final WeChatAuthUtil weChatAuthUtil;

    private final UserInfoService userInfoService;

    private final MqService mqService;

    public void getById(Integer id) {
        userInfoService.getById(id);
    }


    public TokenResp pwLogin(String userName, String pwd) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserName, userName);
        wrapper.eq(UserInfo::getDelStatus, DelStatus.NO.getKey());
        UserInfo userInfo = userInfoService.getOne(wrapper);
        if (userInfo == null) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        checkStatus(userInfo.getStatus());
        if (!PasswordUtil.verifyPassword(pwd, userInfo.getPasswordSalt(), userInfo.getPasswordHash())) {
            throw new BaseException(ErrorCode.USER_PASSWORD_ERROR);
        }
        Long version = userInfo.getVersion();
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getId, userInfo.getId())
                .eq(UserInfo::getVersion, version)
                .set(UserInfo::getVersion, version + 1)
                .set(UserInfo::getLastLoginTime, new Date());
        boolean update = userInfoService.update(updateWrapper);
        if (!update) {
            throw new BaseException(ErrorCode.LOGIN_CONFLICT);
        }
        mqService.loginCouponMsg(userInfo.getId());
        Long uuid = userInfo.getId();
        TokenResp resp = new TokenResp();
        resp.setAccessToken(JwtUtil.generateAccessToken(uuid, version));
        resp.setRefreshToken(JwtUtil.generateRefreshToken(uuid, version));
        resp.setExpiresIn(JwtUtil.EXPIRATION_TIME / 1000);
        return resp;
    }

    private void checkStatus(Integer status) {
        if (UserStatus.DISABLED.getCode().equals(status)) {
            throw new BaseException(ErrorCode.USER_DISABLED);
        }
        if (UserStatus.INACTIVE.getCode().equals(status)) {
            throw new BaseException(ErrorCode.USER_INACTIVE);
        }
        if (!UserStatus.NORMAL.getCode().equals(status)) {
            throw new BaseException(ErrorCode.USER_INACTIVE);
        }
    }


}
