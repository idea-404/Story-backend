package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.main.record.BlogListResponse;
import org.example.story.domain.main.record.ListResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogListService {
    private final BlogRepository blogRepository;

    public BlogListResponse blogFilter(Long lastId, int size, String type){
        List<BlogJpaEntity> blogs =
                blogRepository.findWithCursor(lastId, size, type, true, null, true);
        List<ListResponse> listResponses = setList(blogs);
        return new BlogListResponse(listResponses);
    }


    public List<ListResponse> setList(List<BlogJpaEntity> blogs){
        List<ListResponse> listResponses = blogs.stream()
                .map(c -> new ListResponse(
                        c.getId(),
                        c.getUser().getId(),
                        c.getTitle(),
                        c.getContent(),
                        c.getLike(),
                        c.getView(),
                        c.getComment(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return listResponses;
    }
}
