package com.example.chat.dto;

public class LoginResponse {
    private String code;
    private Data data;

    public LoginResponse(String code, Data data) {
        this.code = code;
        this.data = data;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String token;
        private UserInfo userInfo;

        public Data(String token, UserInfo userInfo) {
            this.token = token;
            this.userInfo = userInfo;
        }

        // Getters
        public String getToken() {
            return token;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }
    }

    public static class UserInfo {
        private String id; // 添加 id 字段
        private String uname;

        public UserInfo(String id, String uname) {
            this.id = id;
            this.uname = uname;
        }

        // Getters
        public String getId() {
            return id;
        }

        public String getUname() {
            return uname;
        }
    }
}