package org.example.story.domain.image.service;

import org.example.story.domain.image.record.response.ImageResponse;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public ImageResponse uploadImage(MultipartFile file) {
        validateImageType(file);
        String fileName = createFileName(file.getOriginalFilename());
        try {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new IllegalStateException("S3 업로드 실패", e);
        }

        return new ImageResponse(
                getPublicUrl(fileName)
        );
    }

    public void deleteImage(String url) {
        String key = extractFileName(url);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String getPublicUrl(String fileName) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toExternalForm();
    }


    private String createFileName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("원본 파일 이름이 비어있습니다.");
        }
        String ext = extractExt(originalName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos < 0) {
            throw new IllegalArgumentException("파일 확장자가 없습니다: " + fileName);
        }
        return fileName.substring(pos + 1);
    }

    private String extractFileName(String imageUrl) {
        try {
            String path = new java.net.URL(imageUrl).getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 형식의 URL입니다: " + imageUrl, e);
        }
    }

    private void validateImageType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다.");
        }

        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (originalName == null || !originalName.toLowerCase().matches(".*\\.(jpg|jpeg)$")) {
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "jpg/jpeg 형식의 파일만 업로드할 수 있습니다.");
        }

        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/jpg"))) {
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "이미지 MIME 타입이 jpg/jpeg가 아닙니다.");
        }
    }
}
