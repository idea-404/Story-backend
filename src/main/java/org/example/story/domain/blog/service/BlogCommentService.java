package org.example.story.domain.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogCommentJpaEntity;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.record.request.BlogCommentRequest;
import org.example.story.domain.blog.record.response.BlogCommentListResponse;
import org.example.story.domain.blog.record.response.BlogCommentResponse;
import org.example.story.domain.blog.repository.BlogCommentRepository;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogCommentService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogCommentRepository blogCommentRepository;

    public BlogCommentResponse createComment(Long userId, BlogCommentRequest request, Long blogId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        BlogCommentJpaEntity comment = BlogCommentJpaEntity.builder()
                .user(user)
                .blog(blog)
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        blogCommentRepository.save(comment);
        return new BlogCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getBlog().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public void deleteComment(Long userId, Long blogId, Long commentId) {
        BlogCommentJpaEntity comment = blogCommentRepository.findByBlogIdAndId(blogId,commentId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        blogCommentRepository.delete(comment);
    }

    public BlogCommentListResponse getComments(Long blogId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
        List<BlogCommentJpaEntity> comments =
                blogCommentRepository.findCommentsAfterCursor(blogId, lastId, pageable);

        List<BlogCommentResponse> responses = comments.stream()
                .map(c -> new BlogCommentResponse(
                        c.getId(),
                        c.getBlog().getId(),
                        c.getUser().getId(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new BlogCommentListResponse(blogId, responses);
    }
}
