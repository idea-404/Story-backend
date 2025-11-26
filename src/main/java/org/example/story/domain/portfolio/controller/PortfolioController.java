package org.example.story.domain.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.image.record.response.ImageResponse;
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
import org.example.story.global.security.auth.AuthUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioQueryService portfolioQueryService;
    private final PortfolioCommentService portfolioCommentService;
    private final AuthUtils authUtils;


    // 포트폴리오 작성
    @PostMapping("/write")
    @RateLimited(limit = 5, durationSeconds = 30)
    public PortfolioResponse createPortfolio(
            @RequestBody PortfolioRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioService.write(userId, request);
    }

    // 수정 준비
    @GetMapping("/edit/{portfolioId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public PortfolioResponse getPortfolio(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioQueryService.getForEdit(userId, portfolioId);
    }

    // 포트폴리오 수정
    @PatchMapping("/edit/{portfolioId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public PortfolioResponse updatePortfolio(
            @PathVariable("portfolioId") Long portfolioId,
            @RequestBody PortfolioRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioService.edit(userId, portfolioId, request);
    }

    // 포트폴리오 조회 (토큰 필요 X)
    @GetMapping("/view/{portfolioId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public PortfolioResponse view(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        return portfolioQueryService.view(portfolioId);
    }

    // 포트폴리오 삭제
    @DeleteMapping("/delete/{portfolioId}")
    @RateLimited(limit = 3, durationSeconds = 30)
    public void deletePortfolio(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        Long userId = authUtils.getCurrentUserId();
        portfolioService.delete(userId, portfolioId);
    }

    // 좋아요 변경
    @PatchMapping("/like/{portfolioId}")
    @RateLimited(limit =10, durationSeconds = 30)
    public PortfolioLikeResponse likeUp(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioService.likeUp(userId, portfolioId);
    }

    // 포트폴리오 공개 여부 토글
    @PatchMapping("/open/{portfolioId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public PortfolioResponse open(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioService.open(userId, portfolioId);
    }

    // 댓글 작성
    @PostMapping("/comment/{portfolioId}")
    @RateLimited(limit = 10, durationSeconds = 30)
    public PortfolioCommentResponse createComment(
            @PathVariable("portfolioId") Long portfolioId,
            @RequestBody PortfolioCommentRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioCommentService.createComment(userId, request, portfolioId);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{portfolioId}/{commentId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public void deleteComment(
            @PathVariable("portfolioId") Long portfolioId,
            @PathVariable("commentId") Long commentId
    ) {
        Long userId = authUtils.getCurrentUserId();
        portfolioCommentService.deleteComment(userId, portfolioId, commentId);
    }

    // 포트폴리오 댓글 조회 (커서 페이징)
    @GetMapping("/comment/{portfolioId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public PortfolioCommentListResponse comment(
            @PathVariable("portfolioId") Long portfolioId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return portfolioCommentService.getComments(portfolioId, lastId, size);
    }

    @PostMapping("/image/upload/{portfolioId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public ImageResponse uploadPortfolioImage(
            @PathVariable Long portfolioId,
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = authUtils.getCurrentUserId();
        return portfolioService.uploadPortfolioImage(userId,portfolioId, file);
    }

    @DeleteMapping("/image/delete/{imageId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public void deletePortfolioImage(@PathVariable Long imageId) {
        Long userId = authUtils.getCurrentUserId();
        portfolioService.deletePortfolioImage(userId, imageId);
    }

    @GetMapping("/image/{portfolioId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public List<ImageResponse> getPortfolioImages(@PathVariable Long portfolioId) {
        return portfolioService.getPortfolioImages(portfolioId);
    }
}
