package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.image.service.ImageService;
import org.example.story.domain.main.record.BlogListResponse;
import org.example.story.domain.main.record.BlogViewsResponse;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogListService {
    private final ImageService imageService;
    private final GenericCursorRepository<BlogJpaEntity> blogCursorRepo;

    public BlogListResponse blogFilter(Long lastId, int size, String type){
        List<String> allowedSortFields = List.of("id","view", "like", "comment");
        if (type != null && !allowedSortFields.contains(type)) {
            throw new ExpectedException(HttpStatus.UNPROCESSABLE_ENTITY,"허용되지 않은 정렬 기준입니다: " + type);
        }
        List<BlogJpaEntity> blogs =
                blogCursorRepo.findWithCursor(lastId, size, type, true, null, null);
        List<BlogViewsResponse> listResponses = setList(blogs);
        return new BlogListResponse(listResponses);
    }


    public List<BlogViewsResponse> setList(List<BlogJpaEntity> blogs){
        List<BlogViewsResponse> listResponses = blogs.stream()
                .map(c -> new BlogViewsResponse(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getNickname(),
                        c.getTitle(),
                        c.getIntroduce(),
                        c.getLike(),
                        c.getView(),
                        c.getComment(),
                        c.getCreatedAt(),
                        imageService.getPublicUrl(c.getThumbnail())
                ))
                .collect(Collectors.toList());

        return listResponses;
    }
}
