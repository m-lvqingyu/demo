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
 * 应用密钥表
 *
 * @author lv
 * @TableName app_secret
 */
@TableName(value = "app_secret")
@Data
public class AppSecret implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 状态: 1-禁用, 5-启用
     */
    private Integer status;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新时间
     */
    private Date updatedTime;
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -8659487886720357434L;
}