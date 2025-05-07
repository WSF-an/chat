package com.example.chat.dto;


import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;


/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    @TableField("userAccount")
    private String userAccount;

    /**
     * 密码
     */
    @TableField("userPassword")
    private String userPassword;

    @TableField("userName")
    private String userName;





    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
