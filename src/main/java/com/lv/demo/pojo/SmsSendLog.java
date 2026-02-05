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
 *
 * @author 18891
 * @TableName sms_send_log
 */
@TableName(value = "sms_send_log")
@Data
public class SmsSendLog implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 模板ID
     */
    private String tempId;

    /**
     * 响应结果
     */
    private String result;

    /**
     * 1:发送失败  5:发送成功
     */
    private Integer status;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 7452978143236693216L;
}