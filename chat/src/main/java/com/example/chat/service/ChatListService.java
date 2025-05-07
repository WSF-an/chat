package com.example.chat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.chat.dto.ChatList;

import java.util.List;

public interface ChatListService extends IService<ChatList> {
    /**
     * 新建一个会话
     */
    boolean addChatList(Long userId, Long conversationId, String description);

    /**
     * 删除一个会话
     *
     */
    //boolean removeChatList(Long userId, Long conversationId);

    /**
     * 获取一个会话
     */
    ChatList getChatList(Long userId, Long conversationId);

    /**
     * 查询某用户下的所有conversationId
     */
    List<ChatList> getChatList(Long userId);


}
