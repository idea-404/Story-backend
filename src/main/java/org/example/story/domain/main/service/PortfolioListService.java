package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.image.service.ImageService;
import org.example.story.domain.main.record.ListResponse;
import org.example.story.domain.main.record.PortfolioListResponse;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioListService {
    private final PortfolioRepository portfolioRepository;
    private final ImageService imageService;

    public PortfolioListResponse portfolioFilter(Long lastId, int size, String type){
        List<String> allowedSortFields = List.of("view", "like", "comment");
        if (type != null && !allowedSortFields.contains(type)) {
            throw new ExpectedException(HttpStatus.UNPROCESSABLE_ENTITY,"허용되지 않은 정렬 기준입니다: " + type);
        }
        List<PortfolioJpaEntity> portfolios =
                portfolioRepository.findWithCursor(lastId, size, type, true, null, true);
        List<ListResponse> listResponses = setList(portfolios);
        return new PortfolioListResponse(listResponses);
    }

    public List<ListResponse> setList(List<PortfolioJpaEntity> portfolios){
        List<ListResponse> listResponses = portfolios.stream()
                .map(c -> new ListResponse(
                        c.getId(),
                        c.getUser().getId(),
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
