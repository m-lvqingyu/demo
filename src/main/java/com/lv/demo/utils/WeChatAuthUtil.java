package com.lv.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Throwables;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.exception.BaseException;
import com.lv.demo.resp.auth.WeChatAccessTokenResp;
import com.lv.demo.resp.auth.WechatUserInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * @author lv
 */
@Slf4j
@Component
public class WeChatAuthUtil {

    private final RestClient restClient;

    public WeChatAuthUtil() {
        this.restClient = RestClient.create();
    }

    @Value("${wechat.app.id}")
    private String appId;

    @Value("${wechat.app.secret}")
    private String appSecret;

    private static final String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    private static final String USER_URL = "https://api.weixin.qq.com/sns/userinfo";

    public WeChatAccessTokenResp getAccessToken(String code) {
        ResponseEntity<JsonNode> response;
        try {
            response = restClient.get().uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                            .queryParam("appid", appId)
                            .queryParam("secret", appSecret)
                            .queryParam("code", code)
                            .queryParam("grant_type", "authorization_code")
                            .build())
                    .retrieve()
                    .toEntity(JsonNode.class);
        } catch (Exception e) {
            log.error("[WeChatAuthUtil]-GetAccessToken Err! msg:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        HttpStatusCode statusCode = response.getStatusCode();
        if (!HttpStatus.OK.equals(statusCode)) {
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        JsonNode body = response.getBody();
        if (body == null) {
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        String accessToken = body.get("access_token").toString();
        Integer expiresIn = Integer.parseInt(body.get("expires_in").toString());
        String refreshToken = body.get("refresh_token").toString();
        String openId = body.get("openid").toString();
        String unionId = body.get("unionid").toString();
        WeChatAccessTokenResp resp = new WeChatAccessTokenResp();
        resp.setAccessToken(accessToken);
        resp.setExpiresIn(expiresIn);
        resp.setRefreshToken(refreshToken);
        resp.setOpenId(openId);
        resp.setUnionId(unionId);
        return resp;
    }

    public WechatUserInfoResp getUserInfo(String token, String openId) {
        ResponseEntity<JsonNode> response;
        try {
            response = restClient.get().uri(uriBuilder -> uriBuilder.path(USER_URL)
                            .queryParam("access_token", token)
                            .queryParam("openid", openId)
                            .build())
                    .retrieve()
                    .toEntity(JsonNode.class);
        } catch (Exception e) {
            log.error("[WeChatAuthUtil]-GetUserInfo Err! msg:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        JsonNode body = response.getBody();
        if (body == null) {
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
        WechatUserInfoResp resp = new WechatUserInfoResp();
        resp.setOpenId(body.get("openid").toString());
        resp.setSex(Integer.valueOf(body.get("sex").toString()));
        resp.setUnionId(body.get("unionid").toString());
        resp.setNickName(body.get("nickname").toString());
        resp.setProvince(body.get("province").toString());
        resp.setCity(body.get("city").toString());
        resp.setCountry(body.get("country").toString());
        resp.setHeadImgUrl(body.get("headimgurl").toString());
        resp.setUnionId(body.get("unionid").toString());
        return resp;
    }


}
