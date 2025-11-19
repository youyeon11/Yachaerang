package com.yachaerang.backend.global.auth.dto.request;

import lombok.*;

public class TokenRequestDto {

    @Getter
    @Setter
    @Builder
    public static class SignupDto {
        private String name;
        private String nickname;
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @Builder
    public static class LoginDto {
        private String email;
        private String password;
    }
}
