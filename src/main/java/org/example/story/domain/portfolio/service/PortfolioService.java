package org.example.story.domain.portfolio.service;

import org.example.story.domain.image.entity.PortfolioImageJpaEntity;
import org.example.story.domain.image.record.request.ImageRequest;
import org.example.story.domain.image.record.response.ImageResponse;
import org.example.story.domain.image.repository.PortfolioImageRepository;
import org.example.story.domain.image.service.ImageService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioLikeJpaEntity;
import org.example.story.domain.portfolio.record.common.PortfolioRequest;
import org.example.story.domain.portfolio.record.common.PortfolioResponse;
import org.example.story.domain.portfolio.record.response.PortfolioLikeResponse;
import org.example.story.domain.portfolio.repository.PortfolioLikeRepository;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.example.story.domain.user.repository.UserRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final ImageService imageService;
    private final PortfolioImageRepository portfolioImageRepository;

    public PortfolioResponse write(Long userId, PortfolioRequest request) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        PortfolioJpaEntity portfolio = PortfolioJpaEntity.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .createdAt(Instant.now())
                .like(0L)
                .view(0L)
                .comment(0L)
                .zerodog(false)
                .build();

        PortfolioJpaEntity saved = portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getZerodog(),
                saved.getCreatedAt()
        );
    }


    public PortfolioResponse edit(Long userId, Long portfolioId, PortfolioRequest request) {
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolio.update(request.title(),request.content());

        PortfolioJpaEntity saved = portfolioRepository.save(portfolio);

        return new PortfolioResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getZerodog(),
                saved.getCreatedAt()
        );
    }


    public void delete(Long userId, Long portfolioId) {
        List<PortfolioImageJpaEntity> images =
                portfolioImageRepository.findByPortfolioId(portfolioId);

        for (PortfolioImageJpaEntity img : images) {
            imageService.deleteImage(img.getImageUrl());
        }
        PortfolioJpaEntity portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolioRepository.delete(portfolio);
    }


    @Transactional
    public PortfolioLikeResponse likeUp(Long userId, Long portfolioId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        boolean liked = portfolioLikeRepository.findByPortfolioAndUser(portfolio, user).isPresent();

        if(liked) {
            portfolio.decreaseLike();
            PortfolioLikeJpaEntity like = portfolioLikeRepository.findByPortfolioAndUser(portfolio, user)
                    .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 기록입니다."));
            portfolioLikeRepository.delete(like);
        } else {
            PortfolioLikeJpaEntity likerecord = PortfolioLikeJpaEntity.builder()
                    .portfolio(portfolio)
                    .user(user)
                    .build();
            portfolioLikeRepository.save(likerecord);
            portfolio.increaseLike();
        }


        return new PortfolioLikeResponse(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getTitle(),
                portfolio.getContent(),
                portfolio.getLike(),
                portfolio.getView(),
                portfolio.getComment(),
                portfolio.getZerodog(),
                portfolio.getCreatedAt(),
                !liked
        );
    }

    @Transactional
    public PortfolioResponse open(Long userId, Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 포트폴리오입니다."));

        portfolio.changeZerodog();
        portfolioRepository.save(portfolio);

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

    public ImageResponse uploadPortfolioImage(MultipartFile file) {
        String fileKey = imageService.uploadImage(file);
        String presignedUrl = imageService.generatePresignedUrl(fileKey);
        return new ImageResponse(fileKey, presignedUrl);
    }

    public void savePortfolioImage(Long userId, Long portfolioId ,ImageRequest request) {

        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"포트폴리오를 찾을 수 없습니다."));

        if(!userId.equals(portfolio.getUser().getId())) {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "업로드 권한이 없습니다.");
        }
        else if(request.fileKeys() == null || request.fileKeys().isEmpty()){
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "저장할 이미지가 없습니다.");
        }
        else {
            for(String key : request.fileKeys()) {
                PortfolioImageJpaEntity image = PortfolioImageJpaEntity.builder()
                        .portfolio(portfolio)
                        .imageUrl(key)
                        .build();
                portfolioImageRepository.save(image);
            }
        }
    }

    public void deletePortfolioImage(Long userId, Long imageId) {

        PortfolioImageJpaEntity image = portfolioImageRepository.findById(imageId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"이미지를 찾을 수 없습니다."));

        PortfolioJpaEntity portfolio = image.getPortfolio();

        if(userId.equals(portfolio.getUser().getId())){
            imageService.deleteImage(image.getImageUrl());

            portfolioImageRepository.delete(image);
        }
        else {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }
    }

    public List<ImageResponse> getPortfolioImages(Long portfolioId) {

        List<PortfolioImageJpaEntity> images =
                portfolioImageRepository.findByPortfolioId(portfolioId);

        return images.stream()
                .map(img -> new ImageResponse(
                        img.getImageUrl(),
                        imageService.generatePresignedUrl(img.getImageUrl())
                ))
                .toList();
    }


}
