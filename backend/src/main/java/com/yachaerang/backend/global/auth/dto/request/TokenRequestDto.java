package com.yachaerang.backend.global.auth.dto.request;

public class TokenRequestDto {

    public static class SignupDto {
        private String name;
        private String nickname;
        private String email;
        private String password;
    }

    public static class LoginDto {
        private String email;
        private String password;
    }
}
