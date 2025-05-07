package com.example.chat.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户会话列表
 * @TableName chat_list
 */
@TableName(value = "chat_list")
@Data
public class ChatList implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private Long conversationId;
    /**
     * 这一次会话的主题
     */
    @TableField("description")
    private String description;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
