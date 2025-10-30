package org.example.story.domain.portfolio.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.record.common.PortfolioRequest;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.record.request.PortfolioCommentRequest;
import org.example.story.domain.portfolio.record.response.PortfolioCommentListResponse;
import org.example.story.domain.portfolio.record.response.PortfolioCommentResponse;
import org.example.story.domain.portfolio.record.response.PortfolioLikeResponse;
import org.example.story.domain.portfolio.service.PortfolioCommentService;
import org.example.story.domain.portfolio.service.PortfolioQueryService;
import org.example.story.domain.portfolio.service.PortfolioService;
import org.example.story.global.aop.RateLimited;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioQueryService portfolioQueryService;
    private final PortfolioCommentService portfolioCommentService;
    private final JwtTokenProvider jwtTokenProvider;


    // 포트폴리오 작성
    @PostMapping("/write")
    @RateLimited(limit = 5, durationSeconds = 60)
    public PortfolioResponse createPortfolio(
            @RequestBody PortfolioRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioService.write(userId, request);
    }

    // 수정 준비
    @GetMapping("/edit/{portfolio_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public PortfolioResponse getPortfolio(
            @PathVariable Long portfolio_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioQueryService.st_edit(userId, portfolio_id);
    }

    // 포트폴리오 수정
    @PatchMapping("/edit/{portfolio_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public PortfolioResponse updatePortfolio(
            @PathVariable Long portfolio_id,
            @RequestBody PortfolioRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioService.edit(userId, portfolio_id, request);
    }

    // 포트폴리오 조회 (토큰 필요 X)
    @GetMapping("/view/{portfolio_id}")
    @RateLimited(limit = 30, durationSeconds = 60)
    public PortfolioResponse view(@PathVariable Long portfolio_id,
                                  HttpServletRequest httpRequest) {
        return portfolioQueryService.view(portfolio_id);
    }

    // 포트폴리오 삭제
    @DeleteMapping("/delete/{portfolio_id}")
    @RateLimited(limit = 3, durationSeconds = 60)
    public void deletePortfolio(
            @PathVariable Long portfolio_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        portfolioService.delete(userId, portfolio_id);
    }

    // 좋아요 변경
    @PatchMapping("/like/{portfolio_id}")
    @RateLimited(limit = 20, durationSeconds = 60)
    public PortfolioLikeResponse likeUp(
            @PathVariable Long portfolio_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioService.likeUp(userId, portfolio_id);
    }

    // 포트폴리오 공개 여부 토글
    @PatchMapping("/open/{portfolio_id}")
    @RateLimited(limit = 5, durationSeconds = 60)
    public PortfolioResponse open(
            @PathVariable Long portfolio_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioService.open(userId, portfolio_id);
    }

    // 댓글 작성
    @PostMapping("/comment/{portfolio_id}")
    @RateLimited(limit = 15, durationSeconds = 60)
    public PortfolioCommentResponse comment(
            @PathVariable Long portfolio_id,
            @RequestBody PortfolioCommentRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        return portfolioCommentService.createComment(userId, request, portfolio_id);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{portfolio_id}/{comment_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public void deleteComment(
            @PathVariable Long portfolio_id,
            @PathVariable Long comment_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = extractUserId(token);
        portfolioCommentService.deleteComment(userId, portfolio_id, comment_id);
    }

    // 포트폴리오 댓글 조회 (커서 페이징)
    @GetMapping("/comment/{portfolio_id}")
    @RateLimited(limit = 30, durationSeconds = 60)
    public PortfolioCommentListResponse comment(
            @PathVariable Long portfolio_id,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return portfolioCommentService.getComments(portfolio_id, lastId, size);
    }

    // 토큰 검증 절차
    public Long extractUserId(String token) {
        if(jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
    }
}
