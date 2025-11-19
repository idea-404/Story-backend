package org.example.story.domain.image.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.image.record.request.ImageUrlRequest;
import org.example.story.domain.image.record.response.ImageResponse;
import org.example.story.domain.image.service.ImageService;
import org.example.story.global.aop.RateLimited;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    @RateLimited(limit = 15, durationSeconds = 30)
    public ImageResponse upload(@RequestPart("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    @DeleteMapping("/delete")
    @RateLimited(limit = 15, durationSeconds = 30)
    public void delete(@RequestBody ImageUrlRequest url) {
        imageService.deleteImage(url);
    }
}
