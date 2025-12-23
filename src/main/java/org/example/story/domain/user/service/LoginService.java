package org.example.story.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.EmailDto;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {
    private final UserRepository userRepository;
    private final SendEmailService sendEmailService;

    public void execute(EmailDto reqDto) {
        String email = reqDto.email();
        if (!userRepository.existsByEmail(email)) {
            throw new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다.");
        }
        String sub = "로그인 이메일 인증";
        String body = """
                <h3>아래 버튼을 클릭하여 로그인을 진행해주세요!</h3>
                <a href="%s" style="background:#4CAF50;color:white;padding:10px 20px;
                                text-decoration:none;border-radius:5px;">이메일 인증하기</a>
                                <p>해당 링크는 10분간만 유효합니다.</p>
                """;
        sendEmailService.execute(email, sub, body);
    }
}
