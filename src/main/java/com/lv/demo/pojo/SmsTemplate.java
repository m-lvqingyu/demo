package com.lv.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 短信模板表
 *
 * @author lv
 */
@TableName(value = "sms_template")
@Data
public class SmsTemplate implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 短信签名名称
     */
    private String signName;

    /**
     * 短信模板CODE
     */
    private String templateCode;

    /**
     * 短信模板变量对应的实际值，JSON格式。如：{"name":"张三","code":"8888"}
     */
    private String templateParam;

    /**
     * 类型：1-登录验证码
     */
    private Integer type;

    /**
     * 平台：1-阿里云
     */
    private Integer platform;

    /**
     * 状态：1-无效  5-有效
     */
    private Integer status;

    /**
     * 删除：1-已删除  5-未删除
     */
    private Integer delStatus;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人ID
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -1790328871213688899L;
}