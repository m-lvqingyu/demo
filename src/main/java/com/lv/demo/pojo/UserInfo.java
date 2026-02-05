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
 * @author lv
 * @TableName user_info
 */
@TableName(value = "user_info")
@Data
public class UserInfo implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 密码盐值
     */
    private String passwordSalt;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别: 0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 微信unionId
     */
    private String unionId;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 状态: 0-禁用 5-未激活 10-正常
     */
    private Integer status;

    /**
     * 是否已验证: 0-未验证 1-已验证
     */
    private Integer isVerified;

    /**
     * 验证时间
     */
    private Date verifiedTime;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 删除：1-已删除  5-未删除
     */
    private Integer delStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -2729235380042949121L;

}