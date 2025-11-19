package com.yachaerang.backend.global.auth.service;

import com.yachaerang.backend.api.common.MemberStatus;
import com.yachaerang.backend.api.common.Role;
import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.auth.dto.request.TokenRequestDto;
import com.yachaerang.backend.global.auth.dto.response.TokenResponseDto;
import com.yachaerang.backend.global.auth.jwt.JwtTokenProvider;
import com.yachaerang.backend.global.auth.util.RefreshTokenUtil;
import com.yachaerang.backend.global.auth.util.UuidGenerator;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;

    // 회원 가입
    public void signup(TokenRequestDto.SignupDto signupDto) {

        // 회원 가입 여부 확인
        if (memberMapper.findByEmail(signupDto.getEmail()) != null){
            log.warn("이미 존재하는 회웝입니다. : {}", signupDto.getEmail());
            throw GeneralException.of(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        log.info("{} :  회원 가입 시작", signupDto.getEmail());
        String encodedPassword = bCryptPasswordEncoder.encode(signupDto.getPassword());
        String memberCode = UuidGenerator.generateUuid(signupDto.getEmail());
        Member member = Member.builder()
                .name(signupDto.getName())
                .email(signupDto.getEmail())
                .memberCode(memberCode)
                .password(encodedPassword)
                .nickname(signupDto.getNickname())
                .memberStatus(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .build();
        memberMapper.save(member);
        log.info("{} : 회원 가입 완료", signupDto.getEmail());
        return;
    }

    // 로그인
    public TokenResponseDto.ResultDto login(
            TokenRequestDto.LoginDto loginDto,
            HttpServletResponse response) {

        // 회원인지 조회
        Member member = memberMapper.findByEmail(loginDto.getEmail());
        if (member == null){
            log.warn("존재하지 않는 회웝입니다. : {}", loginDto.getEmail());
            throw GeneralException.of(ErrorCode.MEMBER_NOT_FOUND);
        }
        // 비밀번호 검증
        log.info("비밀번호 검증 시작...");
        if (!bCryptPasswordEncoder.encode(loginDto.getPassword()).matches(loginDto.getPassword())) {
            throw GeneralException.of(ErrorCode.UNMATCHED_PASSWORD);
        };

        log.info("로그인에 성공");
        String accessToken = jwtTokenProvider.generateAccessToken(member);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member);
        TokenResponseDto.ResultDto responseDto = TokenResponseDto.ResultDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        refreshTokenUtil.saveRefreshToken(member.getMemberCode(), refreshToken);
        refreshTokenUtil.addRefreshTokenCookie(response, member.getMemberCode(), refreshToken);
        return responseDto;
    }

    // 로그아웃
    public void logout(HttpServletResponse response, String token) throws Exception {
        String memberCode = jwtTokenProvider.getMemberCodeFromToken(token);
        refreshTokenUtil.removeRefreshTokenCookie(response);
        refreshTokenUtil.deleteRefreshToken(memberCode);
        return;
    }

    // Access Token 재발급
    public String reissue(String refreshToken) {

        return null;
    }
}
