package org.example.story.domain.main.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.main.record.BlogListResponse;
import org.example.story.domain.main.record.PortfolioListResponse;
import org.example.story.domain.main.record.SearchListResponse;
import org.example.story.domain.main.service.BlogListService;
import org.example.story.domain.main.service.PortfolioListService;
import org.example.story.domain.main.service.SearchService;
import org.example.story.global.aop.RateLimited;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {
    private final SearchService searchService;
    private final BlogListService blogListService;
    private final PortfolioListService portfolioListService;

    @GetMapping("/portfolio/{type}")
    @RateLimited(limit = 30, durationSeconds = 30)
    public PortfolioListResponse getPortfolio(@PathVariable String type,
                                              @RequestParam(required = false) Long lastId,
                                              @RequestParam(defaultValue = "10") int size) {
        return portfolioListService.portfolioFilter(lastId,size, type);
    }

    @GetMapping("/blog/{type}")
    @RateLimited(limit = 30, durationSeconds = 30)
    public BlogListResponse getBlog(@PathVariable String type,
                                         @RequestParam(required = false) Long lastId,
                                         @RequestParam(defaultValue = "10") int size) {
        return blogListService.blogFilter(lastId,size, type);
    }

    @GetMapping("/search")
    @RateLimited(limit = 10, durationSeconds = 30)
    public SearchListResponse search(@RequestParam String keyword,
                                           @RequestParam(required = false) Long lastId,
                                           @RequestParam(defaultValue = "10") int size) {
        return searchService.search(lastId,size, keyword);
    }
}
