package com.yachaerang.backend.api.member.dto.request;

import lombok.*;

public class MemberRequestDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageDto {
        private String name;
        private String nickname;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordDto {
        private String oldPassword;
        private String newPassword;
    }
}
