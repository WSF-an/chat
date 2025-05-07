package com.example.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.chat.dto.ChatList;
import com.example.chat.mapper.ChatListMapper;
import com.example.chat.service.ChatListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList> implements ChatListService {

    @Override
    public ChatList getChatList(Long userId, Long conversationId) {
        ChatList chatList = new ChatList();
        QueryWrapper<ChatList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("conversation_id", conversationId);
        chatList = this.baseMapper.selectOne(queryWrapper);
        if(chatList == null){
            log.info("该会话不存在");
        }
        return chatList;
    }

    @Override
    public boolean addChatList(Long userId, Long conversationId, String description) {
        ChatList charList = new ChatList();
        charList.setUserId(userId);
        charList.setConversationId(conversationId);
        charList.setDescription(description);
        //校验有无重复存入
        QueryWrapper<ChatList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("conversation_id", conversationId);
        ChatList temp = this.baseMapper.selectOne(queryWrapper);
        boolean saveResult;
        if(temp == null) {
            saveResult =this.save(charList);
        }else{
            log.info("已经新建过了");
            saveResult = false;
        }
        return saveResult;


    }

    @Override
    public List<ChatList> getChatList(Long userId) {
        QueryWrapper<ChatList> queryWrapper = new QueryWrapper<ChatList>();
        queryWrapper.eq("user_id", userId);
        List<ChatList> list = this.list(queryWrapper);
        return list;
    }
}
