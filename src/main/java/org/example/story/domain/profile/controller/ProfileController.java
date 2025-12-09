package org.example.story.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.profile.record.response.ViewProfileResDto;
import org.example.story.domain.profile.service.ViewProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ViewProfileService viewProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<ViewProfileResDto> viewProfile(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(
                viewProfileService.execute(id)
        );
    }
}
