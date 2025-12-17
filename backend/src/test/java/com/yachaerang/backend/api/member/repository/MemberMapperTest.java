package com.yachaerang.backend.api.member.repository;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.global.config.MyBatisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql("classpath:H2_schema.sql")
@Import(MyBatisConfig.class)
class MemberMapperTest {

    @Autowired
    MemberMapper memberMapper;

    @Test
    @DisplayName("이메일로 회원 찾기")
    void 이메일로_회원찾기 () {
        // given
        Member member = Member.builder()
                .name("test")
                .nickname("test")
                .email("test@test.com")
                .memberCode("test")
                .password("test")
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .imageUrl("default.png")
                .build();

        memberMapper.save(member);

        // when
        Member foundMember = memberMapper.findByEmail("test@test.com");

        // then
        assertThat(foundMember).isEqualTo(member);
        assertThat(foundMember.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회")
    void 존재하지_않는_이메일() {

        // when
        Member foundMember = memberMapper.findByEmail("test11@test.com");

        // then
        assertThat(foundMember).isNull();
    }

    @Test
    @DisplayName("회원 고유 코드로 조회하기")
    void 회원고유코드로_조회하기() {
        // given
        Member member = Member.builder()
                .id(1L)
                .name("test")
                .memberCode("test")
                .password("test")
                .email("test@test.com")
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .imageUrl("default.png")
                .build();

        memberMapper.save(member);

        // when
        Member foundMember = memberMapper.findByMemberCode("test");

        // then
        assertThat(foundMember).isEqualTo(member);
        assertThat(foundMember.getMemberCode()).isEqualTo("test");
    }

    @Test
    @DisplayName("프로필 이미지 저장 성공")
    void 프로필_이미지_저장_성공() {
        // given
        Member member = Member.builder()
                .name("test")
                .nickname("test")
                .email("test@test.com")
                .memberCode("test")
                .password("test")
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .imageUrl("default.png")
                .build();
        memberMapper.save(member);
        Member savedMember = memberMapper.findByMemberCode("test");
        String newImageUrl = "profile.png";

        // when
        int result = memberMapper.updateProfileImage(newImageUrl, savedMember.getId());

        // then
        assertThat(result).isEqualTo(1);
        String foundImageUrl = memberMapper.findImageUrl(savedMember.getId());
        assertThat(foundImageUrl).isEqualTo(newImageUrl);
    }

    @Test
    @DisplayName("회원 ID로 이미지 조회 성공")
    void 회원ID로_이미지_조회_성공() {
        // given
        Member member = Member.builder()
                .name("test")
                .nickname("test")
                .email("test@test.com")
                .memberCode("test")
                .password("test")
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .imageUrl("default.png")
                .build();
        memberMapper.save(member);
        Member savedMember = memberMapper.findByMemberCode("test");

        // when
        String imageUrl = memberMapper.findImageUrl(savedMember.getId());

        // then
        assertThat(imageUrl).isEqualTo("default.png");
    }

    @Test
    @DisplayName("존재하지 않는 회원의 이미지 수정 시 실패")
    void 존재하지_않는_회원_이미지_수정() {
        // when
        int updatedCount = memberMapper.updateProfileImage("profile.png", 999L);

        // then
        assertThat(updatedCount).isEqualTo(0);
    }
}