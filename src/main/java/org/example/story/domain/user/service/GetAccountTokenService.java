package org.example.story.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.record.common.TokenResDto;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GetAccountTokenService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expire-time}")
    private Long expireTime;

    public TokenResDto execute(String email) {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        UserJpaEntity.builder().email(email).build()
                ));
        return new TokenResDto(
                jwtTokenProvider.createToken(
                        new TokenClaimsDto(
                                email,
                                user.getId(),
                                user.getNickname(),
                                user.getHakburn(),
                                user.getProfileImage(),
                                user.getMajor(),
                                user.getIntroduce(),
                                user.getRole()
                        ), expireTime
                ), user.getRole()
        );
    }
}
