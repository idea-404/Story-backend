package org.example.story.domain.blog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.entity.BlogLikeJpaEntity;
import org.example.story.domain.blog.record.common.BlogRequest;
import org.example.story.domain.blog.record.common.BlogResponse;
import org.example.story.domain.blog.record.response.BlogLikeResponse;
import org.example.story.domain.blog.repository.BlogLikeRepository;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogLikeRepository blogLikeRepository;

    public BlogResponse write(Long userId, BlogRequest request) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        BlogJpaEntity blog = BlogJpaEntity.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .createdAt(Instant.now())
                .like(0L)
                .view(0L)
                .comment(0L)
                .build();

        BlogJpaEntity saved = blogRepository.save(blog);

        return new BlogResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }


    public BlogResponse edit(Long userId, Long blogId, BlogRequest request) {
        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        blog.setTitle(request.title());
        blog.setContent(request.content());

        BlogJpaEntity saved = blogRepository.save(blog);

        return new BlogResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }


    public void delete(Long userId, Long blogId) {
        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        blogRepository.delete(blog);
    }


    @Transactional
    public BlogLikeResponse likeUp(Long userId, Long blogoId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        BlogJpaEntity blog = blogRepository.findById(blogoId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        boolean liked = blogLikeRepository.findByBlogAndUser(blog, user).isPresent();

        if(liked) {
            blog.setLike(blog.getLike() - 1);
            blogLikeRepository.deleteByBlogIdAndUserId(blog.getId(), user.getId());
        } else {
            BlogLikeJpaEntity likerecord = BlogLikeJpaEntity.builder()
                    .blog(blog)
                    .user(user)
                    .build();
            blogLikeRepository.save(likerecord);
            blog.setLike(blog.getLike() + 1);
        }

        blogRepository.save(blog);

        return new BlogLikeResponse(
                blog.getId(),
                blog.getUser().getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt(),
                !liked
        );
    }

}
