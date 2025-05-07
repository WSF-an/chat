package com.example.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.chat.dto.User;
import com.example.chat.dto.vo.UserVO;
import com.example.chat.mapper.UserMapper;
import com.example.chat.service.Userservice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements Userservice {
    @Override
    public long userRegister(String userAccount, String userPassword) {
        //1.校验
        if(StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword)||userAccount.length()<=3||userPassword.length()<5||userPassword.length()>10){//如果注册长度的不合法
            throw new IllegalArgumentException("参数不合法");
        }
        synchronized (userAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if(count > 0){
                throw new IllegalArgumentException("账户已经注册");
            }

            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserName(userAccount.substring(0,1));
            user.setUserPassword(userPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new RuntimeException("数据库出错");
            }
            return user.getId();
        }
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if(StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword)){
            throw new RuntimeException("参数为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", userPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user == null){
            throw new RuntimeException("登录失败,用户不存在");
        }
        request.getSession().setAttribute("user_login", user);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已登录
        Object userLogin = request.getSession().getAttribute("user_login");
        User currentUser = (User) userLogin;
        if(currentUser == null){
            throw new RuntimeException("用户未登录");
        }
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
