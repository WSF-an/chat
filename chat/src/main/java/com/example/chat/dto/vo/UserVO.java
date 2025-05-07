package com.example.chat.dto.vo;


import lombok.Data;

import java.io.Serializable;



@Data
public class UserVO implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;



    private static final long serialVersionUID = 1L;
}