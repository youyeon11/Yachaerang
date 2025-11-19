package com.yachaerang.backend.api.member.repository;

import com.yachaerang.backend.api.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<Member> findById(@Param("id") Long id);

    Member findByEmail(@Param("email") String email);

    List<Member> findAll();

    Member findByMemberCode(@Param("memberCode") String memberCode);

    int save(Member member);

    int update(Member member);

    int delete(Long id);
}
