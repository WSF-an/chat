package com.example.chat.Controller;




import com.example.chat.dto.LoginRequest;
import com.example.chat.dto.LoginResponse;
import com.example.chat.dto.User;
import com.example.chat.dto.UserRegister;
import com.example.chat.dto.vo.UserVO;
import com.example.chat.service.Userservice;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
    @Autowired
    private Userservice userservice;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/user/register")
    public String register(@RequestBody UserRegister user) {
        if(user==null){
            throw new RuntimeException("参数为空");
        }
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        long result  = userservice.userRegister(userAccount,userPassword);
        log.info("注册成功"+result);
        return "注册成功"+String.valueOf(result);
    }

    /**
     * 用户登录
     * @param request
     * @return
     */
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login,HttpServletRequest request) {
        System.out.println("Received login request: " + login.getUname() + ", " + login.getPassword());
        String userAccount = login.getUname();
        String userPassword = login.getPassword();

        UserVO userVO= userservice.userLogin(userAccount,userPassword,request);
        if(userVO!=null){
            // 这里用真实用户信息
            LoginResponse.Data userInfo = new LoginResponse.Data(
                    "fixed-token", // 假设你有一个生成token的方法
                    new LoginResponse.UserInfo(userVO.getId().toString(), userVO.getUserName())
            );
            LoginResponse response = new LoginResponse("0", userInfo);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(new LoginResponse("1", null)); // 返回错误代码
        }
    }



    @GetMapping("/user/get/login")
    public UserVO getLogin(HttpServletRequest request) {
        User user = userservice.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }
}