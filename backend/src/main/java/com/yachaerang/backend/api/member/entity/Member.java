package com.yachaerang.backend.api.member.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String memberCode;

    private String password;

    private MemberStatus  memberStatus;

    private Role role;

    private LocalDateTime inactivatedAt;
}
