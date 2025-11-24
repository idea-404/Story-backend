package org.example.story.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.record.common.TokenDto;
import org.example.story.domain.user.record.request.SignupInformReqDto;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GetInformationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenDto execute(Long userId, SignupInformReqDto reqDto) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."));
        user.updateUserInformation(
                reqDto.nickname(),
                reqDto.studentId().toString(),
                reqDto.profileImage(),
                reqDto.major(),
                reqDto.introduce()
        );
        return new TokenDto(jwtTokenProvider.createToken(new TokenClaimsDto(
                user.getEmail(),
                user.getId(),
                user.getNickname(),
                Long.parseLong(user.getHakburn()),
                user.getProfileImage(),
                user.getMajor(),
                user.getIntroduce(),
                user.getRole()
        ), 60L));
    }
}
