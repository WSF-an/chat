package com.example.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chat.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
