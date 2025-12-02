package com.yachaerang.backend.global.auth.util;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class UuidGenerator {

    public static String generateUuid(String email) {
        try {
            // byte
            byte[] inputBytes = email.getBytes(StandardCharsets.UTF_8);
            // hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(inputBytes);

            // Base64
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(hash)
                    .substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw GeneralException.of(ErrorCode.UUID_GENERATION_FAILED);
        }
    }
}
