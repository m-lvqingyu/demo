package com.lv.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lv.demo.pojo.UserInfo;
import com.lv.demo.service.UserInfoService;
import com.lv.demo.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 18891
* @description 针对表【user_info_0】的数据库操作Service实现
* @createDate 2025-12-26 09:13:29
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {

}




