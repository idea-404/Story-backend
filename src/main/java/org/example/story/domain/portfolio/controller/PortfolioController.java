package org.example.story.domain.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.record.common.PortfolioRequest;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.record.request.PortfolioCommentRequest;
import org.example.story.domain.portfolio.record.response.PortfolioCommentListResponse;
import org.example.story.domain.portfolio.record.response.PortfolioCommentResponse;
import org.example.story.domain.portfolio.record.response.PortfolioLikeResponse;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.portfolio.service.PortfolioService;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final JwtTokenProvider jwtTokenProvider;

    // 포트폴리오 작성
    @PostMapping("/write")
    public PortfolioResponse createPortfolio(@RequestBody PortfolioRequest request, @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        }// JWT에서 가져온 유저 ID
        return portfolioService.write(userId, request);
    }

    // 수정 준비
    @GetMapping("/edit/{portfolio_id}")
    public PortfolioResponse getPortfolio(@PathVariable Long portfolio_id, @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        }// JWT에서 가져온 유저 ID
        return portfolioService.st_edit(userId, portfolio_id);
    }

    // 포트폴리오 수정
    @PatchMapping("/edit/{portfolio_id}")
    public PortfolioResponse updatePortfolio(
            @PathVariable Long portfolio_id,
            @RequestBody PortfolioRequest request,
            @RequestParam String token
    ) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } // JWT에서 가져온 유저 ID
        return portfolioService.edit(userId, portfolio_id, request);
    }

    // 포트폴리오 조회
    @GetMapping("/view/{portfolio_id}")
    public PortfolioResponse view(@PathVariable Long portfolio_id) {
        return portfolioService.view(portfolio_id);
    }

    // 포트폴리오 삭제
    @DeleteMapping("/delete/{portfolio_id}")
    public void deletePortfolio(@PathVariable Long portfolio_id, @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } // JWT에서 가져온 유저 ID
        portfolioService.delete(userId, portfolio_id);
    }

    // 좋아요 변경
    @PatchMapping("/like/{portfolio_id}")
    public PortfolioLikeResponse likeUp(@PathVariable Long portfolio_id, @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } // JWT에서 가져온 userId
        return portfolioService.likeUp(userId, portfolio_id);
    }

    // 포트폴리오 공개 여부 토글
    @PatchMapping("/open/{portfolio_id}")
    public PortfolioResponse open(@PathVariable Long portfolio_id, @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } // JWT에서 가져온 userId
        return portfolioService.open(userId, portfolio_id);
    }

    // 댓글 작성
    @PostMapping("/comment/{portfolio_id}")
    public PortfolioCommentResponse comment(@PathVariable Long portfolio_id,
                                            @RequestBody PortfolioCommentRequest request,
                                            @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        }
        return portfolioService.createComment(userId, request, portfolio_id);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{portfolio_id/{comment_id}")
    public void deleteComment(@PathVariable Long portfolio_id,
                              @PathVariable Long comment_id,
                              @RequestParam String token) {
        Long userId = null;
        if(jwtTokenProvider.validateToken(token)) {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        }
        portfolioService.deleteComment(userId, portfolio_id, comment_id);
    }

    // 포트폴리오 댓글 조회 (커서 페이징)
    @GetMapping("/comment/{portfolio_id}")
    public PortfolioCommentListResponse comment(
            @PathVariable Long portfolio_id,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return portfolioService.getComments(portfolio_id, lastId, size);
    }
}
