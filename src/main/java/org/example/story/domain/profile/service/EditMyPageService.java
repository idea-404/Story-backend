package org.example.story.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.profile.record.request.EditMyPageReqDto;
import org.example.story.domain.profile.record.response.EditMyPageResDto;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EditMyPageService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expire-time}")
    private Long expireTime;

    public EditMyPageResDto execute(Long userId, EditMyPageReqDto reqDto) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        user.updateUserInformation(
                reqDto.nickname(),
                reqDto.studentId(),
                reqDto.profileImage(),
                reqDto.major(),
                reqDto.introduce()
        );
        String token = jwtTokenProvider.createToken(new TokenClaimsDto(
                user.getEmail(),
                user.getId(),
                user.getNickname(),
                user.getHakburn(),
                user.getProfileImage(),
                user.getMajor(),
                user.getIntroduce(),
                user.getRole()
        ), expireTime);

        return new EditMyPageResDto(token, user.getRole());
    }
}
