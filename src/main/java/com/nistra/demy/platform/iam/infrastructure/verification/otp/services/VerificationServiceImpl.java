package com.nistra.demy.platform.iam.infrastructure.verification.otp.services;

import com.nistra.demy.platform.iam.infrastructure.verification.otp.OtpSecureVerificationService;
import com.nistra.demy.platform.iam.infrastructure.verification.otp.configuration.VerificationProperties;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements OtpSecureVerificationService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final VerificationProperties properties;

    public VerificationServiceImpl(VerificationProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateCode() {
        int max = (int) Math.pow(10, properties.getCodeLength()) - 1;
        int code = secureRandom.nextInt(max + 1);
        return String.format("%0" + properties.getCodeLength() + "d", code);
    }

    @Override
    public String generateCode(int length) {
        int max = (int) Math.pow(10, length) - 1;
        int code = secureRandom.nextInt(max + 1);
        return String.format("%0" + length + "d", code);
    }

    @Override
    public Integer generateExpirationMinutes() {
        return properties.getExpirationMinutes();
    }

    @Override
    public boolean verifyCode(String code, String expectedCode, LocalDateTime expirationTime) {
        if (code == null || expectedCode == null || expirationTime == null) return false;
        if (LocalDateTime.now().isAfter(expirationTime)) return false; // Código expirado
        return code.equals(expectedCode);
    }
}