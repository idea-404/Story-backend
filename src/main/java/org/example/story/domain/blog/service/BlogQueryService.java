package org.example.story.domain.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.record.common.BlogResponse;
import org.example.story.domain.blog.record.response.BlogViewResponse;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogQueryService {
    private final BlogRepository blogRepository;

    public BlogResponse getForEdit(Long userId, Long blogId){
        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));
        return new BlogResponse(
                blog.getId(),
                blog.getUser().getNickname(),
                blog.getTitle(),
                blog.getIntroduce(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt()
        );
    }

    @Transactional
    public BlogViewResponse view(Long blogId){
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));

        blog.increaseView();

        return new BlogViewResponse(
                blog.getId(),
                blog.getUser().getNickname(),
                blog.getUser().getHakburn(),
                blog.getTitle(),
                blog.getIntroduce(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt()
        );
    }
}
