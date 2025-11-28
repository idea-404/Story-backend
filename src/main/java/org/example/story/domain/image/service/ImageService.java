package org.example.story.domain.image.service;

import org.example.story.global.error.exception.ExpectedException;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.presigned-url-duration-minutes}")
    private long presignedUrlDuration;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/gif");

    public String uploadImage(MultipartFile file) {
        validateImageType(file);

        String fileName = createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다.");
        }

        return fileName;
    }

    public String uploadThumbnail(MultipartFile file) {
        validateImageType(file);

        String key = "thumbnail/" + createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "썸네일 업로드 실패");
        }

        return key;
    }

    public String generatePresignedUrl(String fileName) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(builder ->
                        builder.signatureDuration(Duration.ofMinutes(presignedUrlDuration))
                                .getObjectRequest(getObjectRequest)
                );

        return presignedRequest.url().toString();
    }

    public void deleteImage(String fileName) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }


    private String createFileName(String originalName) {
        String ext = extractExt(originalName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos < 0) throw new ExpectedException(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다: " + fileName);
        return fileName.substring(pos + 1);
    }

    private void validateImageType(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "파일 없음");

        String name = file.getOriginalFilename();
        String type = file.getContentType();

        if (name == null || !name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"))
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "jpg, jpeg, png, gif 파일만 허용됩니다.");

        if (type == null || !ALLOWED_MIME_TYPES.contains(type.toLowerCase()))
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "MIME 타입 오류");
    }

    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket,
                region,
                key
        );
    }
}
