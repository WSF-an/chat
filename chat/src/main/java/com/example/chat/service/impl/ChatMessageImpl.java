package com.example.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.chat.dto.ChatMessage;
import com.example.chat.mapper.ChatMessageMapper;
import com.example.chat.service.ChatListService;
import com.example.chat.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ChatMessageImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatListService chatListService;

    @Override
    public List<ChatMessage> getChatMessages(Long conversationId, long userId) {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId);
        queryWrapper.eq("user_id", userId);
        List<ChatMessage> messages =  this.list(queryWrapper);
        if(messages == null){
            log.info("数据库无记录");
        }
        return messages;
    }

//    @Override
//    public List<ChatMessage> getChatMessage(Long conversationId, Long userId) {
//        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("conversationID", conversationId);
//        queryWrapper.eq("userId", userId);
//        List<ChatMessage> messages =  this.list(queryWrapper);
//        if(messages == null){
//            log.info("数据库无记录");
//        }
//        return messages;
//    }

    @Override
    public ChatMessage addChatMessage(String message, Long conversationId,long userId) {
        String reply = "AI：" + message + "（我已经收到了）";
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversationId(conversationId);
        chatMessage.setReply(reply);
        chatMessage.setMessage(message);
        chatMessage.setUserId(userId);
        Date time = new Date();
        chatMessage.setTime(time);
        this.save(chatMessage);
        return chatMessage;
    }
}
