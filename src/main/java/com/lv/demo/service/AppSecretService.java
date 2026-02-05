package com.lv.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lv.demo.pojo.AppSecret;

/**
 * @author 18891
 * @description 针对表【app_secret(应用密钥表)】的数据库操作Service
 * @createDate 2025-12-23 15:18:25
 */
public interface AppSecretService extends IService<AppSecret> {

    /**
     * 根据AppId获取应用密钥
     *
     * @param appId AppId
     * @return 应用密钥信息
     */
    String getByCache(String appId);

}
