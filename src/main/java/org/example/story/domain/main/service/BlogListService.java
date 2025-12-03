package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.image.service.ImageService;
import org.example.story.domain.main.record.BlogListResponse;
import org.example.story.domain.main.record.BlogViewResponse;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogListService {
    private final BlogRepository blogRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    public BlogListResponse blogFilter(Long lastId, int size, String type){
        List<String> allowedSortFields = List.of("view", "like", "comment");
        if (type != null && !allowedSortFields.contains(type)) {
            throw new ExpectedException(HttpStatus.UNPROCESSABLE_ENTITY,"허용되지 않은 정렬 기준입니다: " + type);
        }
        List<BlogJpaEntity> blogs =
                blogRepository.findWithCursor(lastId, size, type, true, null, null);
        List<BlogViewResponse> listResponses = setList(blogs);
        return new BlogListResponse(listResponses);
    }


    public List<BlogViewResponse> setList(List<BlogJpaEntity> blogs){
        List<BlogViewResponse> listResponses = blogs.stream()
                .map(c -> new BlogViewResponse(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getNickname(),
                        c.getTitle(),
                        c.getContent(),
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
