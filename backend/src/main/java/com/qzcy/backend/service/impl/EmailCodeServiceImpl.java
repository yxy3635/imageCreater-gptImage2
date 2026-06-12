package com.qzcy.backend.service.impl;

import com.qzcy.backend.exception.BusinessException;
import com.qzcy.backend.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements EmailCodeService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.dev-return-code:true}")
    private boolean devReturnCode;

    @Override
    public Map<String, Object> sendCode(String email, String scene) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedScene = normalizeScene(scene);
        String code = String.valueOf(100000 + RANDOM.nextInt(900000));
        codes.put(key(normalizedEmail, normalizedScene), new CodeEntry(code, LocalDateTime.now().plusMinutes(10)));

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailHost != null && !mailHost.isBlank() && mailSender != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom == null || mailFrom.isBlank() ? null : mailFrom);
            message.setTo(normalizedEmail);
            message.setSubject("imageCreater 邮箱验证码");
            message.setText("你的验证码是：" + code + "，10分钟内有效。");
            mailSender.send(message);
        }

        if (devReturnCode || mailHost == null || mailHost.isBlank()) {
            return Map.of("sent", true, "devCode", code);
        }
        return Map.of("sent", true);
    }

    @Override
    public void verify(String email, String scene, String code) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedScene = normalizeScene(scene);
        CodeEntry entry = codes.get(key(normalizedEmail, normalizedScene));
        if (entry == null || entry.expireAt().isBefore(LocalDateTime.now()) || !entry.code().equals(code)) {
            throw new BusinessException(400, "邮箱验证码无效或已过期");
        }
        codes.remove(key(normalizedEmail, normalizedScene));
    }

    private String normalizeEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        if (!normalized.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException(400, "邮箱格式不正确");
        }
        return normalized;
    }

    private String normalizeScene(String scene) {
        String normalized = scene == null ? "" : scene.trim();
        if (!"register".equals(normalized) && !"forgot_password".equals(normalized)) {
            throw new BusinessException(400, "验证码场景无效");
        }
        return normalized;
    }

    private String key(String email, String scene) {
        return scene + ":" + email;
    }

    private record CodeEntry(String code, LocalDateTime expireAt) {
    }
}
