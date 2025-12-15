package com.yachaerang.backend.api.member.repository;

import com.yachaerang.backend.api.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    Member findById(@Param("id") Long id);

    Member findByEmail(@Param("email") String email);

    List<Member> findAll();

    Member findByMemberCode(@Param("memberCode") String memberCode);

    int save(Member member);

    int updateProfile(@Param("memberId") Long memberId,
                      @Param("name") String name,
                      @Param("nickname") String nickname);

    int delete(Long id);

    // 이미지 저장하기
    int updateProfileImage(
            @Param("imageUrl") String imageUrl,
            @Param("memberId") Long memberId
    );

    // 이미지만 찾기
    String findImageUrl(@Param("memberId") Long memberId);
}
