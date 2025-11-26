package org.example.story.domain.blog.controller;

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
import org.example.story.domain.image.record.response.ImageResponse;
import org.example.story.global.aop.RateLimited;
import org.example.story.global.security.auth.AuthUtils;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;
    private final BlogQueryService blogQueryService;
    private final BlogCommentService blogCommentService;
    private final AuthUtils authUtils;

    // 포트폴리오 작성
    @PostMapping("/write")
    @RateLimited(limit = 5, durationSeconds = 30)
    public BlogResponse createBlog(
            @RequestBody BlogRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return blogService.write(userId, request);
    }

    // 수정 준비
    @GetMapping("/edit/{blogId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public BlogResponse getBlog(
            @PathVariable("blogId") Long blogId
    ) {
        Long userId = authUtils.getCurrentUserId();
        return blogQueryService.getForEdit(userId, blogId);
    }

    // 포트폴리오 수정
    @PatchMapping("/edit/{blogId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public BlogResponse updateBlog(
            @PathVariable("blogId") Long blogId,
            @RequestBody BlogRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return blogService.edit(userId, blogId, request);
    }

    // 포트폴리오 조회 (토큰 필요 X)
    @GetMapping("/view/{blogId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public BlogResponse view(@PathVariable Long blogId) {
        return blogQueryService.view(blogId);
    }

    // 포트폴리오 삭제
    @DeleteMapping("/delete/{blogId}")
    @RateLimited(limit = 3, durationSeconds = 30)
    public void deleteBlog(
            @PathVariable("blogId") Long blogId
    ) {
        Long userId = authUtils.getCurrentUserId();
        blogService.delete(userId, blogId);
    }

    // 좋아요 변경
    @PatchMapping("/like/{blogId}")
    @RateLimited(limit = 10, durationSeconds = 30)
    public BlogLikeResponse likeUp(
            @PathVariable("blogId") Long blogId
    ) {
        Long userId = authUtils.getCurrentUserId();
        return blogService.likeUp(userId, blogId);
    }


    // 댓글 작성
    @PostMapping("/comment/{blogId}")
    @RateLimited(limit = 10, durationSeconds = 30)
    public BlogCommentResponse createComment(
            @PathVariable("blogId") Long blogId,
            @RequestBody BlogCommentRequest request
    ) {
        Long userId = authUtils.getCurrentUserId();
        return blogCommentService.createComment(userId, request, blogId);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{blogId}/{commentId}")
    @RateLimited(limit = 5, durationSeconds = 30)
    public void deleteComment(
            @PathVariable("blogId") Long blogId,
            @PathVariable("commentId") Long commentId
    ) {
        Long userId = authUtils.getCurrentUserId();
        blogCommentService.deleteComment(userId, blogId, commentId);
    }

    // 포트폴리오 댓글 조회 (커서 페이징)
    @GetMapping("/comment/{blogId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public BlogCommentListResponse comment(
            @PathVariable("blogId") Long blogId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return blogCommentService.getComments(blogId, lastId, size);
    }

    @PostMapping("/image/upload/{blogId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public ImageResponse uploadBlogImage(
            @PathVariable Long blogId,
            @RequestPart("file") MultipartFile file
    ) {
        return blogService.uploadBlogImage(blogId, file);
    }

    @DeleteMapping("/image/delete/{imageId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public void deleteBlogImage(@PathVariable Long imageId) {
        blogService.deleteBlogImage(imageId);
    }

    @GetMapping("/image/{blogId}")
    @RateLimited(limit = 20, durationSeconds = 30)
    public List<ImageResponse> getBlogImages(@PathVariable Long blogId) {
        return blogService.getBlogImages(blogId);
    }
}
