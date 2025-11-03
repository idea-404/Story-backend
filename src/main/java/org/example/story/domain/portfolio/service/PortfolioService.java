package org.example.story.domain.portfolio.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogLikeJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioLikeJpaEntity;
import org.example.story.domain.portfolio.record.common.PortfolioRequest;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.record.response.PortfolioLikeResponse;
import org.example.story.domain.portfolio.repository.PortfolioLikeRepository;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;

    public PortfolioResponse write(Long userId, PortfolioRequest request) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        PortfolioJpaEntity portfolio = PortfolioJpaEntity.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .createdAt(Instant.now())
                .like(0L)
                .view(0L)
                .comment(0L)
                .zerodog(false)
                .build();

        PortfolioJpaEntity saved = portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getZerodog(),
                saved.getCreatedAt()
        );
    }


    public PortfolioResponse edit(Long userId, Long portfolioId, PortfolioRequest request) {
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolio.update(request.title(),request.content());

        PortfolioJpaEntity saved = portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getZerodog(),
                saved.getCreatedAt()
        );
    }


    public void delete(Long userId, Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        portfolioRepository.delete(portfolio);
    }


    @Transactional
    public PortfolioLikeResponse likeUp(Long userId, Long portfolioId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        boolean liked = portfolioLikeRepository.findByPortfolioAndUser(portfolio, user).isPresent();

        if(liked) {
            portfolio.decreaseLike();
            PortfolioLikeJpaEntity like = portfolioLikeRepository.findByPortfolioAndUser(portfolio, user)
                    .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 기록입니다."));
            portfolioLikeRepository.delete(like);
        } else {
            PortfolioLikeJpaEntity likerecord = PortfolioLikeJpaEntity.builder()
                    .portfolio(portfolio)
                    .user(user)
                    .build();
            portfolioLikeRepository.save(likerecord);
            portfolio.increaseLike();
        }


        return new PortfolioLikeResponse(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getTitle(),
                portfolio.getContent(),
                portfolio.getLike(),
                portfolio.getView(),
                portfolio.getComment(),
                portfolio.getZerodog(),
                portfolio.getCreatedAt(),
                !liked
        );
    }


    public PortfolioResponse open(Long userId, Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolio.setZerodog(!portfolio.getZerodog());
        portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getTitle(),
                portfolio.getContent(),
                portfolio.getLike(),
                portfolio.getView(),
                portfolio.getComment(),
                portfolio.getZerodog(),
                portfolio.getCreatedAt()
        );
    }

}
