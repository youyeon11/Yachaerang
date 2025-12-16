package com.yachaerang.backend.infrastructure.smtp.service;

import com.yachaerang.backend.api.member.entity.Member;
import com.yachaerang.backend.api.member.repository.MemberMapper;
import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import com.yachaerang.backend.infrastructure.smtp.dto.request.MailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String username;

    private static final long CODE_EXPIRATION_MINUTES = 5;
    private static final String VERIFICATION_CODE_PREFIX = "email:verification:";

    /*
    인증 코드 자동 생성 및 Redis 저장
     */
    private String createCode(String email) {
        int code = new java.security.SecureRandom().nextInt(900000) + 100000;
        String key = VERIFICATION_CODE_PREFIX + email;

        stringRedisTemplate.opsForValue().set(
                key,
                String.valueOf(code),
                CODE_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
        return String.valueOf(code);
    }

    /*
    메일을 전송 하기 위한 동기 작업
     */
    public void requestVerificationMail(MailRequestDto.MailRequest request) {
        String email = request.getMail();

        if (memberMapper.findByEmail(email) != null) {
            throw GeneralException.of(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
        String code = createCode(email);
        sendVerificationMailAsync(email, code);
        return;
    }

    public MimeMessage createMail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(email);
            helper.setSubject("이메일 인증 번호");
            String body =
                    "<h2>야채랑에 오신걸 환영합니다!</h2>" +
                            "<h3>아래의 인증번호를 입력하세요.</h3>" +
                            "<h1>" + code + "</h1>" +
                            "<h3>감사합니다.</h3>";
            helper.setText(body, true);
            return message;
        } catch (MessagingException e) {
            LogUtil.error("메일 전송에 실패하였습니다.", e.getMessage());
            throw GeneralException.of(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    /*
    이메일 전송(비동기)
     */
    @Async("asyncExecutor")
    public void sendVerificationMailAsync(String email, String code) {
        MimeMessage message = createMail(email, code);
        javaMailSender.send(message);
    }

    /*
    이메일 인증 코드 검증
     */
    public boolean isVerified(MailRequestDto.VerificationRequest request) {
        String email = request.getMail();

        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (storedCode == null) {
            return false;
        }
        boolean isValid = storedCode.equals(String.valueOf(request.getCode()));

        // 인증 성공 시 삭제
        if (isValid) {
            LogUtil.info("이메일 인증 성공 완료 -> 삭제");
            stringRedisTemplate.delete(key);
        }
        return isValid;
    }

    /*
    인증 코드 검증 후 임시 비밀번호 발급
     */
    public void verifyAndSendTempPassword(MailRequestDto.VerificationRequest request) {
        String email = request.getMail();
        String inputCode = request.getCode();

        // 인증 코드 검증
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (storedCode == null) {
            throw GeneralException.of(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        if (!storedCode.equals(String.valueOf(request.getCode()))) {
            throw GeneralException.of(ErrorCode.UNMATCHED_VERIFICATION_CODE);
        }

        // 회원 존재 여부 확인
        Member member = memberMapper.findByEmail(email);
        if (member == null) {
            throw GeneralException.of(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 인증 코드 삭제
        stringRedisTemplate.delete(key);

        // 임시 비밀번호 생성 및 DB 저장
        String tempPassword = generateTempPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(tempPassword);
        memberMapper.updatePassword(email, encodedPassword);

        sendTempPasswordMail(email, tempPassword);
    }

    /*
    임시 비밀번호 생성
     */
    private String generateTempPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + numbers + specialChars;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        sb.append(upperCase.charAt(random.nextInt(upperCase.length())));
        sb.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        sb.append(numbers.charAt(random.nextInt(numbers.length())));
        sb.append(specialChars.charAt(random.nextInt(specialChars.length())));
        for (int i = 0; i < 8; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] password = sb.toString().toCharArray();
        for (int i = password.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }
        return new String(password);
    }

    @Async("asyncExecutor")
    public void sendTempPasswordMail(String email, String tempPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(email);
            helper.setSubject("임시 비밀번호 발급 안내");
            String body = """
                <div style="font-family: 'Apple SD Gothic Neo', sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">임시 비밀번호가 발급되었습니다.</h2>
                    <p>아래의 임시 비밀번호로 로그인 후 반드시 비밀번호를 변경해주세요.</p>
                    <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <p style="margin: 0; font-size: 14px; color: #666;">임시 비밀번호</p>
                        <p style="margin: 10px 0 0 0; font-size: 24px; font-weight: bold; color: #333; letter-spacing: 2px;">%s</p>
                    </div>
                    <p style="color: #e74c3c; font-size: 14px;">⚠️ 보안을 위해 로그인 후 즉시 비밀번호를 변경해주세요.</p>
                    <p style="color: #999; font-size: 12px;">본인이 요청하지 않은 경우, 이 메일을 무시해주세요.</p>
                </div>
                """.formatted(tempPassword);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LogUtil.error("이메일 발송 실패", e.getMessage());
            throw GeneralException.of(ErrorCode.FAILED_GENERATE_PASSWORD);
        }
    }

    /**
     * 비밀번호 초기화를 위한 검증
     * @param request
     */
    public void requestPasswordResetVerificationMail(MailRequestDto.MailRequest request) {
        String email = request.getMail();

        Member member = memberMapper.findByEmail(email);
        if (member == null) {
            throw GeneralException.of(ErrorCode.MEMBER_NOT_FOUND);
        }

        String code = createCode(email);
        sendVerificationMailAsync(email, code);
    }
}
