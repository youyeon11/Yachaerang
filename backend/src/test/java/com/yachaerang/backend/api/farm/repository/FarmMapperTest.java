package com.yachaerang.backend.api.farm.repository;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.farm.entity.Farm;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.config.MyBatisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@Sql("classpath:H2_schema.sql")
@Import(MyBatisConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class FarmMapperTest {

    @Autowired private MemberMapper memberMapper;
    @Autowired private FarmMapper farmMapper;

    private Long memberId;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .email("test@example.com")
                .name("테스트")
                .nickname("테스트유저")
                .memberCode("testCode")
                .imageUrl("testImageUrl")
                .password("testPassword")
                .role(Role.ROLE_USER)
                .memberStatus(MemberStatus.ACTIVE)
                .build();

        memberMapper.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("농장 저장 성공")
    void 농장_저장_성공() {
        // given
        Farm farm = Farm.builder()
                .memberId(memberId)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .comment("좋은 농장입니다")
                .build();
        // when
        int result = farmMapper.save(farm);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(farm.getId()).isNotNull();
    }

    @Test
    @DisplayName("농장 저장 후 ID 생성 확인")
    void 농장생성후_ID_생성_확인() {
        // given
        Farm farm = Farm.builder()
                .memberId(memberId)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .comment("좋은 농장입니다")
                .build();

        // when
        farmMapper.save(farm);

        // then
        assertThat(farm.getId()).isNotNull();
        assertThat(farm.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("memberId로 농장 조회 성공")
    void memberId로_농장조회_성공() {
        // given
        Farm farm = Farm.builder()
                .memberId(memberId)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .comment("좋은 농장입니다")
                .build();
        farmMapper.save(farm);

        // when
        Farm found = farmMapper.findByMemberId(memberId);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getMemberId()).isEqualTo(memberId);
        assertThat(found.getManpower()).isEqualTo(farm.getManpower());
        assertThat(found.getLocation()).isEqualTo(farm.getLocation());
        assertThat(found.getCultivatedArea()).isEqualTo(farm.getCultivatedArea());
        assertThat(found.getFlatArea()).isEqualTo(farm.getFlatArea());
        assertThat(found.getMainCrop()).isEqualTo(farm.getMainCrop());
        assertThat(found.getGrade()).isEqualTo(farm.getGrade());
        assertThat(found.getComment()).isEqualTo(farm.getComment());
    }

    @Test
    @DisplayName("존재하지 않는 농장")
    void 존재하지_않는_농장() {
        // given
        Long nonExistentMemberId = 9999L;

        // when
        Farm found = farmMapper.findByMemberId(nonExistentMemberId);

        // then
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("농장 정보 수정 성공")
    void 농장정보_수정_성공() {
        // given
        Farm farm = Farm.builder()
                .memberId(memberId)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .comment("좋은 농장입니다")
                .build();
        farmMapper.save(farm);

        farm.setManpower(10);
        farm.setLocation("강원도 횡성");
        farm.setCultivatedArea(2000.0);
        farm.setFlatArea(1800.0);
        farm.setMainCrop("토마토");
        farm.setGrade("A");
        farm.setComment("매우 훌륭한 농장입니다");

        // when
        int result = farmMapper.updateFarm(farm);

        // then
        assertThat(result).isEqualTo(1);

        Farm updated = farmMapper.findByMemberId(memberId);
        assertThat(updated.getManpower()).isEqualTo(10);
        assertThat(updated.getLocation()).isEqualTo("강원도 횡성");
        assertThat(updated.getCultivatedArea()).isEqualTo(2000.0);
        assertThat(updated.getFlatArea()).isEqualTo(1800.0);
        assertThat(updated.getMainCrop()).isEqualTo("토마토");
        assertThat(updated.getGrade()).isEqualTo("A");
        assertThat(updated.getComment()).isEqualTo("매우 훌륭한 농장입니다");
    }

    @Test
    @DisplayName("평가 결과만 수정 성공")
    void updateEvaluation_Success() {
        // given
        Farm farm = Farm.builder()
                .memberId(memberId)
                .manpower(5)
                .location("rural")
                .cultivatedArea(1000.0)
                .flatArea(800.0)
                .mainCrop("딸기")
                .grade("B")
                .farmType("좋은 유형")
                .comment("좋은 농장입니다")
                .build();
        farmMapper.save(farm);
        Long farmId = farm.getId();

        // when
        int result = farmMapper.updateEvaluation(farmId, "D", "부족한 유형", "좀만 더 가꿔봐요!");

        // then
        assertThat(result).isEqualTo(1);

        Farm updated = farmMapper.findByMemberId(memberId);
        assertThat(updated.getGrade()).isEqualTo("D");
        assertThat(updated.getFarmType()).isEqualTo("부족한 유형");
        assertThat(updated.getComment()).isEqualTo("좀만 더 가꿔봐요!");
        // 다른 필드는 변경되지 않음
        assertThat(updated.getManpower()).isEqualTo(farm.getManpower());
        assertThat(updated.getLocation()).isEqualTo(farm.getLocation());
    }

}