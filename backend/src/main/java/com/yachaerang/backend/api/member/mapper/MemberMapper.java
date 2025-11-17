package com.yachaerang.backend.api.member.mapper;

import com.yachaerang.backend.api.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    Member findById(@Param("id") Long id);

    Member findByEmail(@Param("email") String email);

    List<Member> findAll();

    int insert(Member member);

    int update(Member member);

    int delete(Long id);
}
