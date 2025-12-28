package org.example.story.domain.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogCommentJpaEntity;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.record.request.BlogCommentRequest;
import org.example.story.domain.blog.record.response.BlogCommentListResponse;
import org.example.story.domain.blog.record.response.BlogCommentResponse;
import org.example.story.domain.blog.record.response.BlogCommentViewResponse;
import org.example.story.domain.blog.repository.BlogCommentRepository;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogCommentService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogCommentRepository blogCommentRepository;

    @Transactional
    public BlogCommentResponse createComment(Long userId, BlogCommentRequest request, Long blogId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));
        BlogCommentJpaEntity comment = BlogCommentJpaEntity.builder()
                .user(user)
                .blog(blog)
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        blog.increaseComment();
        blogCommentRepository.save(comment);
        return new BlogCommentResponse(
                comment.getId(),
                comment.getBlog().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long userId, Long blogId, Long commentId) {
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));
        BlogCommentJpaEntity comment = blogCommentRepository.findByBlogIdAndId(blogId,commentId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다.");
        }
        blog.decreaseComment();
        blogCommentRepository.delete(comment);
    }

    public BlogCommentListResponse getComments(Long blogId) {
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));
        List<BlogCommentJpaEntity> comments =
                blogCommentRepository.findByBlogOrderByIdDesc(blog);

        List<BlogCommentViewResponse> responses = comments.stream()
                .map(c -> new BlogCommentViewResponse(
                        c.getId(),
                        c.getBlog().getId(),
                        c.getUser().getNickname(),
                        c.getUser().getHakburn(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new BlogCommentListResponse(blogId, responses);
    }
}
