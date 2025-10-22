package org.example.story.domain.portfolio.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.entity.PortfolioCommentJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioLikeJpaEntity;
import org.example.story.domain.portfolio.record.common.PortfolioRequest;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.record.request.PortfolioCommentRequest;
import org.example.story.domain.portfolio.record.response.PortfolioCommentListResponse;
import org.example.story.domain.portfolio.record.response.PortfolioCommentResponse;
import org.example.story.domain.portfolio.record.response.PortfolioLikeResponse;
import org.example.story.domain.portfolio.repository.PortfolioCommentRepository;
import org.example.story.domain.portfolio.repository.PortfolioLikeRepository;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;

    public PortfolioResponse write(Long userId, PortfolioRequest request) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

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

    public PortfolioResponse st_edit(Long userId, Long portfolioId){
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
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

    public PortfolioResponse edit(Long userId, Long portfolioId, PortfolioRequest request) {
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));

        portfolio.setTitle(request.title());
        portfolio.setContent(request.content());

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

    public PortfolioResponse view(Long portfolioId){
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));

        portfolioRepository.incrementView(portfolio.getId());

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

    public void delete(Long userId, Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public PortfolioLikeResponse likeUp(Long userId, Long portfolioId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));

        boolean liked = portfolioLikeRepository.findByPortfolioAndUser(portfolio, user).isPresent();

        if(liked) {
            portfolio.setLike(portfolio.getLike() - 1);
            portfolioLikeRepository.deleteByPortfolioIdAndUserId(portfolio.getId(), user.getId());
        } else {
            PortfolioLikeJpaEntity likerecord = PortfolioLikeJpaEntity.builder()
                    .portfolio(portfolio)
                    .user(user)
                    .build();
            portfolioLikeRepository.save(likerecord);
            portfolio.setLike(portfolio.getLike() + 1);
        }

        portfolioRepository.save(portfolio);

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));

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

    public PortfolioCommentResponse createComment(Long userId, PortfolioCommentRequest request, Long portfolioId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
        PortfolioCommentJpaEntity comment = PortfolioCommentJpaEntity.builder()
                .user(user)
                .portfolio(portfolio)
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        portfolioCommentRepository.save(comment);
        return new PortfolioCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getPortfolio().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public void deleteComment(Long userId, Long portfolioId, Long commentId) {
        PortfolioCommentJpaEntity comment = portfolioCommentRepository.findByPortfolioIdAndId(portfolioId,commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
        portfolioCommentRepository.delete(comment);
    }

    public PortfolioCommentListResponse getComments(Long portfolioId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
        List<PortfolioCommentJpaEntity> comments =
                portfolioCommentRepository.findCommentsAfterCursor(portfolioId, lastId, pageable);

        List<PortfolioCommentResponse> responses = comments.stream()
                .map(c -> new PortfolioCommentResponse(
                        c.getId(),
                        c.getPortfolio().getId(),
                        c.getUser().getId(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new PortfolioCommentListResponse(portfolioId, responses);
    }

}
