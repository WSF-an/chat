package com.example.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chat.dto.ChatList;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ChatListMapper extends BaseMapper<ChatList> {
}
