package org.example.story.domain.main.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.main.record.ListResponse;
import org.example.story.domain.main.record.SearchListResponse;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PortfolioRepository portfolioRepository;
    private final BlogRepository blogRepository;
    private final PortfolioListService portfolioListService;
    private final BlogListService blogListService;

    public SearchListResponse search(Long lastId, int size, String keyword){
        List<PortfolioJpaEntity> portfolios =
                portfolioRepository.findWithCursor(lastId, size, null, true, keyword, true);
        List<ListResponse> portfolioListResponses = portfolioListService.setList(portfolios);
        List<BlogJpaEntity> blogs =
                blogRepository.findWithCursor(lastId, size, null, true, keyword, null);
        List<ListResponse> blogListResponses = blogListService.setList(blogs);

        return new SearchListResponse(portfolioListResponses, blogListResponses);
    }
}
