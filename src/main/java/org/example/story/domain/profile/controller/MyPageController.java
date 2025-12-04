package org.example.story.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.profile.record.request.EditMyPageReqDto;
import org.example.story.domain.profile.record.response.EditMyPageResDto;
import org.example.story.domain.profile.record.response.ViewMyPageResDto;
import org.example.story.domain.profile.service.EditMyPageService;
import org.example.story.domain.profile.service.ViewMyPageService;
import org.example.story.global.security.auth.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {
    private final AuthUtils authUtils;
    private final ViewMyPageService viewMyPageService;
    private final EditMyPageService editMyPageService;

    @GetMapping("/")
    public ResponseEntity<ViewMyPageResDto> viewMyPage() {
        Long userId = authUtils.getCurrentUserId();
        return ResponseEntity.ok().body(
                viewMyPageService.execute(userId)
        );
    }

    @PatchMapping("/jeongbo")
    public ResponseEntity<EditMyPageResDto> editMyPage(
            @RequestBody EditMyPageReqDto reqDto
    ) {
        Long userId = authUtils.getCurrentUserId();
        return ResponseEntity.ok().body(
                editMyPageService.execute(userId, reqDto)
        );
    }
}
