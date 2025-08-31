package com.nistra.demy.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record VerificationCode(
        String code,
        LocalDateTime expiration
) {
    public VerificationCode {
        if (code != null && code.isBlank())
            throw new IllegalArgumentException("Verification code cannot be empty");
        if (expiration != null && expiration.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Expiration date cannot be in the past");
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }

    public boolean matches(String inputCode) {
        return code != null && code.equals(inputCode) && !isExpired();
    }
}
