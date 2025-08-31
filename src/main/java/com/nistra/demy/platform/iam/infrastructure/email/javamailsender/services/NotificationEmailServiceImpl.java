package com.nistra.demy.platform.iam.infrastructure.email.javamailsender.services;

import com.nistra.demy.platform.iam.infrastructure.email.javamailsender.UserNotificationEmailService;
import com.nistra.demy.platform.shared.infrastructure.email.javamailsender.TemplatedEmailService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationEmailServiceImpl implements UserNotificationEmailService {

    private final TemplatedEmailService templatedEmailService;

    public NotificationEmailServiceImpl(TemplatedEmailService templatedEmailService) {
        this.templatedEmailService = templatedEmailService;
    }

    @Override
    public void sendVerificationEmail(String to, String code, int expirationMinutes) {
        sendTemplatedEmail(to, "Confirm your Demy account", "email/verification-email",
                Map.of("code", code, "expirationMinutes", expirationMinutes));
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        sendTemplatedEmail(to, "Recover your password", "email/reset-password-email",
                Map.of("resetLink", resetLink));
    }

    private void sendTemplatedEmail(String to, String subject, String template, Map<String, Object> variables) {
        templatedEmailService.sendEmail(to, subject, template, variables);
    }
}
