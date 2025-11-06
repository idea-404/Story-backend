package org.example.story.domain.portfolio.service;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioQueryService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioResponse getForEdit(Long userId, Long portfolioId){
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getTitle(),
                portfolio.getContent(),
                portfolio.getLike(),
                portfolio.getView(),
                portfolio.getComment(),
                portfolio.getZerodog(),
                portfolio.getCreatedAt()
        );
    }

    @Transactional
    public PortfolioResponse view(Long portfolioId){
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolio.increaseView();

        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getTitle(),
                portfolio.getContent(),
                portfolio.getLike(),
                portfolio.getView(),
                portfolio.getComment(),
                portfolio.getZerodog(),
                portfolio.getCreatedAt()
        );
    }
}


