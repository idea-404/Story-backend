package org.example.story.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailService {
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    @Value("${base.url}")
    private String url;

    @Async
    public void execute(
            String email, String subject, String body
    ) {
        String token = jwtTokenProvider.createToken(
                new TokenClaimsDto(
                        email,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ), 10L
        );
        String verifyUrl = url + "/api/v1/auth/verify?token=" + token;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body.formatted(verifyUrl), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", email, e);
        }
    }
}
