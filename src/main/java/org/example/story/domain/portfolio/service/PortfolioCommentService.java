package org.example.story.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.entity.PortfolioCommentJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.record.request.PortfolioCommentRequest;
import org.example.story.domain.portfolio.record.response.PortfolioCommentListResponse;
import org.example.story.domain.portfolio.record.response.PortfolioCommentResponse;
import org.example.story.domain.portfolio.record.response.PortfolioCommentViewResponse;
import org.example.story.domain.portfolio.repository.PortfolioCommentRepository;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioCommentService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioCommentRepository portfolioCommentRepository;

    @Transactional
    public PortfolioCommentResponse createComment(Long userId, PortfolioCommentRequest request, Long portfolioId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        PortfolioCommentJpaEntity comment = PortfolioCommentJpaEntity.builder()
                .user(user)
                .portfolio(portfolio)
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        portfolioCommentRepository.save(comment);
        portfolioRepository.incrementComment(portfolio.getId());
        return new PortfolioCommentResponse(
                comment.getId(),
                comment.getPortfolio().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long userId, Long portfolioId, Long commentId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        PortfolioCommentJpaEntity comment = portfolioCommentRepository.findByPortfolioIdAndId(portfolioId,commentId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다.");
        }
        portfolioCommentRepository.delete(comment);
        portfolioRepository.decrementComment(portfolio.getId());

    }

    public PortfolioCommentListResponse getComments(Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        List<PortfolioCommentJpaEntity> comments =
                portfolioCommentRepository.findByPortfolioOrderByIdDesc(portfolio);

        List<PortfolioCommentViewResponse> responses = comments.stream()
                .map(c -> new PortfolioCommentViewResponse(
                        c.getId(),
                        c.getPortfolio().getId(),
                        c.getUser().getNickname(),
                        c.getUser().getHakburn(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new PortfolioCommentListResponse(portfolioId, responses);
    }
}
