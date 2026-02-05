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
public class WechatUserInfoResp {

    private String openId;

    private String nickName;

    private Integer sex;

    private String province;

    private String city;

    private String country;

    private String headImgUrl;

    private String unionId;
    
}
