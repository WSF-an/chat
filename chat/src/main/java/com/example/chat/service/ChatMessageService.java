package com.example.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.chat.dto.ChatMessage;


import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    /**
     *查找一条记录
     */
    //List<ChatMessage> getChatMessage(Long conversationId, Long userId);
    /**
     * 添加一条记录
     */
    ChatMessage addChatMessage(String message, Long conversationId,long userId);
    /**
     * 获取用户某ConversationID下的所有消息记录
     */
    List<ChatMessage> getChatMessages(Long conversationId, long userId);
}
