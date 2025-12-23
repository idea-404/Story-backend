package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.main.record.BlogViewsResponse;
import org.example.story.domain.main.record.PortfolioViewsResponse;
import org.example.story.domain.main.record.SearchListResponse;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PortfolioListService portfolioListService;
    private final BlogListService blogListService;
    private final GenericCursorRepository<BlogJpaEntity> blogCursorRepo;
    private final GenericCursorRepository<PortfolioJpaEntity> portfolioCursorRepo;

    public SearchListResponse search(Long lastId, int size, String keyword){
        List<PortfolioJpaEntity> portfolios =
                portfolioCursorRepo.findWithCursor(lastId, size, null, true, keyword, true);
        List<PortfolioViewsResponse> portfolioListResponses = portfolioListService.setList(portfolios);
        List<BlogJpaEntity> blogs =
                blogCursorRepo.findWithCursor(lastId, size, null, true, keyword, null);
        List<BlogViewsResponse> blogListResponses = blogListService.setList(blogs);

        return new SearchListResponse(portfolioListResponses, blogListResponses);
    }
}
