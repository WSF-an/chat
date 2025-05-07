package com.example.chat.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 一组聊天消息
 * @TableName chat_message
 */
@TableName(value = "chat_message")
@Data
public class ChatMessage implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 问题
     */
    @TableField("message")
    private String message;

    /**
     * AI回复
     */
    @TableField("reply")
    private String reply;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private long conversationId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private long userId;

    /**
     * 会话时间
     */
    @TableField("time")
    private Date time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
