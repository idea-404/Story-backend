package org.example.story.domain.image.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Value("${spring.minio.endpoint}")
    private String endpoint;

    @Value("${spring.minio.presigned-url-duration-minutes}")
    private long presignedUrlDuration;

    private static final String THUMBNAIL_DIRECTORY = "thumbnail/";
    private static final String IMAGE_DIRECTORY = "post/";

    private static final Set<String> ALLOWED_MIME_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif");

    public String uploadImage(MultipartFile file) {
        validateImageType(file);

        String key = IMAGE_DIRECTORY + createFileName(file.getOriginalFilename());

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ExpectedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "이미지 업로드에 실패했습니다."
            );
        }

        return key;
    }

    public String uploadThumbnail(MultipartFile file) {
        validateImageType(file);

        String key = THUMBNAIL_DIRECTORY + createFileName(file.getOriginalFilename());

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ExpectedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "썸네일 업로드에 실패했습니다."
            );
        }

        return key;
    }

    public String generatePresignedUrl(String key) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .method(Method.GET)
                            .expiry(Math.toIntExact(presignedUrlDuration * 60))
                            .build()
            );
        } catch (Exception e) {
            throw new ExpectedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Presigned URL 생성 실패"
            );
        }
    }

    public String getPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return endpoint + "/" + bucket + "/" + key;
    }

    public void deleteImage(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            throw new ExpectedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "이미지 삭제 실패"
            );
        }
    }

    private String createFileName(String originalName) {
        String ext = extractExt(originalName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos < 0) {
            throw new ExpectedException(
                    HttpStatus.BAD_REQUEST,
                    "파일 확장자가 없습니다."
            );
        }
        return fileName.substring(pos + 1).toLowerCase();
    }

    private void validateImageType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "파일 이름이 없습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new ExpectedException(
                    HttpStatus.BAD_REQUEST,
                    "허용되지 않는 이미지 타입입니다."
            );
        }
    }
}
