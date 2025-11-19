package org.example.story.domain.image.record.request;

import org.springframework.web.multipart.MultipartFile;

public record ImageFileRequest(
        MultipartFile imageFile
) {

}
