package org.example.story.domain.blog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.entity.BlogLikeJpaEntity;
import org.example.story.domain.blog.record.common.BlogRequest;
import org.example.story.domain.blog.record.common.BlogResponse;
import org.example.story.domain.blog.record.response.BlogLikeResponse;
import org.example.story.domain.blog.repository.BlogLikeRepository;
import org.example.story.domain.blog.repository.BlogRepository;
import org.example.story.domain.image.entity.BlogImageJpaEntity;
import org.example.story.domain.image.record.response.ImageKeyResponse;
import org.example.story.domain.image.record.response.ImageResponse;
import org.example.story.domain.image.repository.BlogImageRepository;
import org.example.story.domain.image.service.ImageService;
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
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final ImageService imageService;
    private final BlogImageRepository blogImageRepository;

    public BlogResponse write(Long userId, BlogRequest request) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        BlogJpaEntity blog = BlogJpaEntity.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .createdAt(Instant.now())
                .like(0L)
                .view(0L)
                .comment(0L)
                .thumbnail(request.thumbnail())
                .build();

        BlogJpaEntity saved = blogRepository.save(blog);

        return new BlogResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }


    public BlogResponse edit(Long userId, Long blogId, BlogRequest request) {
        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));

        blog.update(request.title(), request.content(), request.thumbnail());

        BlogJpaEntity saved = blogRepository.save(blog);

        return new BlogResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getLike(),
                saved.getView(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }


    public void delete(Long userId, Long blogId) {
        List<BlogImageJpaEntity> images =
                blogImageRepository.findByBlogId(blogId);

        for (BlogImageJpaEntity img : images) {
            imageService.deleteImage(img.getImageUrl());
        }

        BlogJpaEntity blog = blogRepository.findByIdAndUserId(blogId, userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));
        if (blog.getThumbnail() != null) {
            imageService.deleteImage(blog.getThumbnail());
        }
        blogRepository.delete(blog);
    }


    @Transactional
    public BlogLikeResponse likeUp(Long userId, Long blogId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 블로그입니다."));

        boolean liked = blogLikeRepository.findByBlogAndUser(blog, user).isPresent();

        if(liked) {
            blog.decreaseLike();
            BlogLikeJpaEntity like = blogLikeRepository.findByBlogAndUser(blog, user)
                    .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND, "존재하지 않는 기록  ㅠ ㅕ ㅛㅛ ㅛ ㅛ ㅎ   퓨입니다."));
            blogLikeRepository.delete(like);
        } else {
            BlogLikeJpaEntity likerecord = BlogLikeJpaEntity.builder()
                    .blog(blog)
                    .user(user)
                    .build();
            blogLikeRepository.save(likerecord);
            blog.increaseLike();
        }

        return new BlogLikeResponse(
                blog.getId(),
                blog.getUser().getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getLike(),
                blog.getView(),
                blog.getComment(),
                blog.getCreatedAt(),
                !liked
        );
    }

    public ImageResponse uploadBlogImage(Long userId, Long blogId, MultipartFile file) {

        BlogJpaEntity blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"블로그를 찾을 수 없습니다."));

        if(userId.equals(blog.getUser().getId())){
            String fileKey = imageService.uploadImage(file);

            BlogImageJpaEntity entity = BlogImageJpaEntity.builder()
                    .blog(blog)
                    .imageUrl(fileKey)
                    .build();

            blogImageRepository.save(entity);
            String presignedUrl = imageService.generatePresignedUrl(fileKey);
            return new ImageResponse(fileKey, presignedUrl);
        }
        else {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "업로드 권한이 없습니다.");
        }

    }

    public void deleteBlogImage(Long userId,Long imageId) {

        BlogImageJpaEntity image = blogImageRepository.findById(imageId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"이미지를 찾을 수 없습니다."));

        BlogJpaEntity blog = image.getBlog();

        if(userId.equals(blog.getUser().getId())) {
            imageService.deleteImage(image.getImageUrl());

            blogImageRepository.delete(image);
        }
        else {
            throw new ExpectedException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

    }

    public List<ImageResponse> getBlogImages(Long blogId) {

        List<BlogImageJpaEntity> images =
                blogImageRepository.findByBlogId(blogId);

        return images.stream()
                .map(img -> new ImageResponse(
                        img.getImageUrl(),
                        imageService.generatePresignedUrl(img.getImageUrl())
                ))
                .toList();
    }

    public ImageKeyResponse uploadBlogThumbnail(MultipartFile file) {
        return new ImageKeyResponse(imageService.uploadThumbnail(file));
    }
}
