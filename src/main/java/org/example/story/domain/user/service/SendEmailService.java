package org.example.story.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEmailService {
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

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
        String verifyUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject.formatted(verifyUrl));
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ExpectedException(
                    HttpStatus.BAD_REQUEST, "메일 전송에 실패하였습니다 : " + e.getMessage());
        }
    }
}
