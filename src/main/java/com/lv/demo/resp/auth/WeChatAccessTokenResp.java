package com.lv.demo.resp.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WeChatAccessTokenResp {

    private String accessToken;

    private Integer expiresIn;

    private String refreshToken;

    private String openId;

    private String unionId;

}
