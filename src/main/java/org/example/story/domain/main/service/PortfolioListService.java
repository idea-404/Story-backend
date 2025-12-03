package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.main.record.PortfolioListResponse;
import org.example.story.domain.main.record.PortfolioViewResponse;
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

    public PortfolioListResponse portfolioFilter(Long lastId, int size, String type){
        List<String> allowedSortFields = List.of("view", "like", "comment");
        if (type != null && !allowedSortFields.contains(type)) {
            throw new ExpectedException(HttpStatus.UNPROCESSABLE_ENTITY,"허용되지 않은 정렬 기준입니다: " + type);
        }
        List<PortfolioJpaEntity> portfolios =
                portfolioRepository.findWithCursor(lastId, size, type, true, null, true);
        List<PortfolioViewResponse> listResponses = setList(portfolios);
        return new PortfolioListResponse(listResponses);
    }

    public List<PortfolioViewResponse> setList(List<PortfolioJpaEntity> portfolios){
        List<PortfolioViewResponse> listResponses = portfolios.stream()
                .map(c -> new PortfolioViewResponse(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getNickname(),
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
