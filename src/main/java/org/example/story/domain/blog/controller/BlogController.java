package org.example.story.domain.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.record.common.BlogRequest;
import org.example.story.domain.blog.record.common.BlogResponse;
import org.example.story.domain.blog.record.request.BlogCommentRequest;
import org.example.story.domain.blog.record.response.BlogCommentListResponse;
import org.example.story.domain.blog.record.response.BlogCommentResponse;
import org.example.story.domain.blog.record.response.BlogLikeResponse;
import org.example.story.domain.blog.service.BlogCommentService;
import org.example.story.domain.blog.service.BlogQueryService;
import org.example.story.domain.blog.service.BlogService;
import org.example.story.global.aop.RateLimited;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;
    private final BlogQueryService blogQueryService;
    private final BlogCommentService blogCommentService;
    private final JwtTokenProvider jwtTokenProvider;


    // 포트폴리오 작성
    @PostMapping("/write")
    @RateLimited(limit = 5, durationSeconds = 60)
    public BlogResponse createBlog(
            @RequestBody BlogRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        return blogService.write(userId, request);
    }

    // 수정 준비
    @GetMapping("/edit/{blog_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public BlogResponse getBlog(
            @PathVariable Long blog_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        return blogQueryService.getForEdit(userId, blog_id);
    }

    // 포트폴리오 수정
    @PatchMapping("/edit/{blog_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public BlogResponse updateBlog(
            @PathVariable Long blog_id,
            @RequestBody BlogRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        return blogService.edit(userId, blog_id, request);
    }

    // 포트폴리오 조회 (토큰 필요 X)
    @GetMapping("/view/{blog_id}")
    @RateLimited(limit = 30, durationSeconds = 60)
    public BlogResponse view(@PathVariable Long blog_id,
                                  HttpServletRequest httpRequest) {
        return blogQueryService.view(blog_id);
    }

    // 포트폴리오 삭제
    @DeleteMapping("/delete/{blog_id}")
    @RateLimited(limit = 3, durationSeconds = 60)
    public void deleteBlog(
            @PathVariable Long blog_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        blogService.delete(userId, blog_id);
    }

    // 좋아요 변경
    @PatchMapping("/like/{blog_id}")
    @RateLimited(limit = 20, durationSeconds = 60)
    public BlogLikeResponse likeUp(
            @PathVariable Long blog_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        return blogService.likeUp(userId, blog_id);
    }


    // 댓글 작성
    @PostMapping("/comment/{blog_id}")
    @RateLimited(limit = 15, durationSeconds = 60)
    public BlogCommentResponse comment(
            @PathVariable Long blog_id,
            @RequestBody BlogCommentRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        return blogCommentService.createComment(userId, request, blog_id);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{blog_id}/{comment_id}")
    @RateLimited(limit = 10, durationSeconds = 60)
    public void deleteComment(
            @PathVariable Long blog_id,
            @PathVariable Long comment_id,
            HttpServletRequest httpRequest
    ) {
        String token = (String) httpRequest.getAttribute("token");
        Long userId = jwtTokenProvider.extractUserId(token);
        blogCommentService.deleteComment(userId, blog_id, comment_id);
    }

    // 포트폴리오 댓글 조회 (커서 페이징)
    @GetMapping("/comment/{blog_id}")
    @RateLimited(limit = 30, durationSeconds = 60)
    public BlogCommentListResponse comment(
            @PathVariable Long blog_id,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return blogCommentService.getComments(blog_id, lastId, size);
    }
}
