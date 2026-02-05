package com.lv.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lv.demo.pojo.SmsTemplate;
import com.lv.demo.service.SmsTemplateService;
import com.lv.demo.mapper.SmsTemplateMapper;
import org.springframework.stereotype.Service;

/**
* @author 18891
* @description 针对表【sms_template(短信模板表)】的数据库操作Service实现
* @createDate 2025-12-04 20:25:01
*/
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate>
    implements SmsTemplateService{

}




