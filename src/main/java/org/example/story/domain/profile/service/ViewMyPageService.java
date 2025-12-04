package org.example.story.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.profile.record.common.BlogCommonDto;
import org.example.story.domain.profile.record.common.PortfolioCommonDto;
import org.example.story.domain.profile.record.response.ViewMyPageResDto;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewMyPageService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final BlogRepository blogRepository;

    public ViewMyPageResDto execute(Long userId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        List<PortfolioJpaEntity> portfolio = portfolioRepository.findByUserId(userId);
        List<BlogJpaEntity> blog = blogRepository.findByUserId(userId);

        List<PortfolioCommonDto> portfolioResult = portfolio.stream()
                .map(PortfolioCommonDto::new)
                .toList();

        List<BlogCommonDto> blogResult = blog.stream()
                .map(BlogCommonDto::new)
                .toList();

        return new ViewMyPageResDto(
                user.getNickname(),
                user.getHakburn(),
                user.getIntroduce(),
                user.getProfileImage(),
                portfolioResult,
                blogResult
        );
    }
}
