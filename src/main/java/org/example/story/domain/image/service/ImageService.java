package org.example.story.domain.image.service;

import org.example.story.domain.image.record.request.ImageUrlRequest;
import org.example.story.domain.image.record.response.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
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

    @Value("${cloud.aws.region.static}")
    private String region;

    public ImageResponse uploadImage(MultipartFile file) {

        String fileName = createFileName(file.getOriginalFilename());
        try {
            // 메타데이터 설정
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)  // 공개 URL로 접근시키려면 필요
                    .build();

            // 업로드 실행
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return new ImageResponse(
                getPublicUrl(fileName)
        );
    }

    public void deleteImage(ImageUrlRequest url) {
        String key = extractFileName(url.imageUrl());

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket) // 버킷 이름
                .key(key)            // URL에서 추출한 키
                .build();

        s3Client.deleteObject(request);
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, fileName);
    }


    private String createFileName(String originalName) {
        String ext = extractExt(originalName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }

    private String extractFileName(String imageUrl) {
        int index = imageUrl.lastIndexOf("/") + 1;
        return imageUrl.substring(index);
    }
}
