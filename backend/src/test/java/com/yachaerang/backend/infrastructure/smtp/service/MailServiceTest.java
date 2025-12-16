package com.yachaerang.backend.infrastructure.smtp.service;

import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.infrastructure.smtp.dto.request.MailRequestDto;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock private MemberMapper memberMapper;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private JavaMailSender javaMailSender;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private MimeMessage mimeMessage;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private MailService mailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";
    private static final String VERIFICATION_CODE_PREFIX = "email:verification:";
    private static final String TEST_USERNAME = "sender@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "username", TEST_USERNAME);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("이메일 발송 성공")
    void 이메일_발송_성공() {
        // given
        MailRequestDto.MailRequest request = MailRequestDto.MailRequest.builder()
                .mail(TEST_EMAIL)
                .build();
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);

        // when
        CompletableFuture<String> result = mailService.sendMail(request);

        // then
        assertThat(result).isCompletedWithValue(TEST_CODE);
        then(javaMailSender).should().send(any(MimeMessage.class));
        then(valueOperations).should().set(
                eq(VERIFICATION_CODE_PREFIX + TEST_EMAIL),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("인증 코드 Redis 저장 성공")
    void 인증코드_Redis_저장_성공() {
        // given
        MailRequestDto.MailRequest request = new MailRequestDto.MailRequest();
        ReflectionTestUtils.setField(request, "mail", TEST_EMAIL);

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        // when
        mailService.sendMail(request);

        // then
        then(valueOperations).should().set(
                eq(VERIFICATION_CODE_PREFIX + TEST_EMAIL),
                codeCaptor.capture(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );

        String capturedCode = codeCaptor.getValue();
        assertThat(capturedCode)
                .hasSize(6)
                .containsOnlyDigits();

        int code = Integer.parseInt(capturedCode);
        assertThat(code).isBetween(100000, 999999);
    }

    @Test
    @DisplayName("인증코드 검증 성공")
    void 인증코드_검증_성공() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);

        // when
        boolean result = mailService.isVerified(request);

        // then
        assertThat(result).isTrue();
        then(stringRedisTemplate).should().delete(VERIFICATION_CODE_PREFIX + TEST_EMAIL);
    }

    @Test
    @DisplayName("인증코드 불일치 시 실패")
    void 인증코드_불일치시_실패() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code("999999")
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);

        // when
        boolean result = mailService.isVerified(request);

        // then
        assertThat(result).isFalse();
        then(stringRedisTemplate).should(never()).delete(anyString());
    }

    @Test
    @DisplayName("저장된 인증코드가 없으면 실패")
    void 저장된_인증코드가_없으면_실패() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(null);

        // when
        boolean result = mailService.isVerified(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("인증성공 시 Redis에서 코드 삭제")
    void 인증성공_시_Redis에서_코드_삭제() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);

        // when
        mailService.isVerified(request);

        // then
        then(stringRedisTemplate).should().delete(VERIFICATION_CODE_PREFIX + TEST_EMAIL);
    }

    @Test
    @DisplayName("임시 비밀번호 발급 성공")
    void 임시_비밀번호_발급_성공() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code(TEST_CODE)
                .build();
        Member member = Member.builder()
                .name(TEST_USERNAME)
                .nickname(TEST_USERNAME)
                .email(TEST_EMAIL)
                .memberCode("test")
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);
        given(memberMapper.findByEmail(TEST_EMAIL)).willReturn(member);
        given(bCryptPasswordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        assertThatCode(() -> mailService.verifyAndSendTempPassword(request))
                .doesNotThrowAnyException();

        // then
        then(stringRedisTemplate).should().delete(VERIFICATION_CODE_PREFIX + TEST_EMAIL);
        then(memberMapper).should().updatePassword(eq(TEST_EMAIL), eq("encodedPassword"));
        then(javaMailSender).should().send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("인증코드 불일치 시 예외")
    void 인증코드_불일치시_예외() {
        // given
        MailRequestDto.VerificationRequest request = MailRequestDto.VerificationRequest
                .builder()
                .mail(TEST_EMAIL)
                .code("999999")
                .build();
        given(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).willReturn(TEST_CODE);

        // when & then
        assertThatThrownBy(() -> mailService.verifyAndSendTempPassword(request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> {
                    GeneralException ge = (GeneralException) ex;
                    assertThat(ge.getErrorCode()).isEqualTo(ErrorCode.UNMATCHED_VERIFICATION_CODE);
                });
    }

    @Test
    @DisplayName("MimeMessage 생성 성공")
    void MimeMessage_생성_성공() {
        // given
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        MimeMessage result = mailService.createMail(TEST_EMAIL);

        // then
        assertThat(result).isNotNull();
        then(valueOperations).should().set(
                eq(VERIFICATION_CODE_PREFIX + TEST_EMAIL),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
    }
}