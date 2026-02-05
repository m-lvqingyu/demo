package com.lv.demo.biz;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.base.Throwables;
import com.lv.demo.pojo.UserInfo;
import com.lv.demo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UserBiz {

    private final UserInfoService userInfoService;

    @SentinelResource(value = "getUserById",
            blockHandler = "getUserByIdHandleBlock")
    public UserInfo getUserById(Long id) {
        return userInfoService.getById(id);
    }

    /**
     * 熔断降级处理
     *
     * @param id 用户ID
     * @param ex 异常信息
     */
    public Boolean getUserByIdHandleBlock(Long id, BlockException ex) {
        log.error("[GetUserById]-进入熔断降级逻辑 id:{}， msg:{}",
                id,
                Throwables.getStackTraceAsString(ex));
        return false;
    }

}
