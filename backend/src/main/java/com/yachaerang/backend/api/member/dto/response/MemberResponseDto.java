package com.yachaerang.backend.api.member.dto.response;

import lombok.*;

public class MemberResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindDto {
        private long id;
        private String memberCode;
        private String email;
        private String name;
        private String nickname;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageDto {
        private String email;
        private String name;
        private String nickname;
    }
}
