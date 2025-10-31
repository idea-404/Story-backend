package org.example.story.domain.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.record.common.BlogResponse;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogQueryService {
    private final BlogRepository blogRepository;

    public BlogResponse st_edit(Long userId, Long blogId){
        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        return new BlogResponse(
                blog.getId(),
                blog.getUser().getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt()
        );
    }

    public BlogResponse view(Long blogId){
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        blogRepository.incrementView(blog.getId());

        return new BlogResponse(
                blog.getId(),
                blog.getUser().getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt()
        );
    }
}
