package com.example.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chat.dto.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
