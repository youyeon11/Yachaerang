package com.yachaerang.backend.api.member.dto.response;

public class MemberResponseDto {

    public static class FindDto {
        private long id;
        private String memberCode;
        private String email;
        private String name;
        private String nickname;
    }
}
