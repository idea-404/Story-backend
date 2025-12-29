package org.example.story.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.EmailDto;
import org.example.story.domain.user.record.common.TokenResDto;
import org.example.story.domain.user.record.request.SignupInformReqDto;
import org.example.story.domain.user.service.*;
import org.example.story.global.security.auth.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {
    private final AuthUtils authUtils;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final VerifyService verifyService;
    private final OAuthJwtService oAuthJwtService;
    private final GetInformationService getInformationService;

    @PostMapping("/sign")
    public ResponseEntity<Void> sign(
            @RequestBody @Valid EmailDto reqDto) {
        signUpService.execute(reqDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid EmailDto reqDto) {
        loginService.execute(reqDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenResDto> verify(
            @RequestParam String token) {
        TokenResDto dto = verifyService.execute(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/oauth/success")
    public ResponseEntity<TokenResDto> oauthSuccess(
            Authentication authentication) {
        TokenResDto dto = oAuthJwtService.execute(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/signup/inform")
    public ResponseEntity<TokenResDto> information(
            @RequestBody @Valid SignupInformReqDto reqDto
            ) {
        Long userId = authUtils.getCurrentUserId();
        TokenResDto dto = getInformationService.execute(userId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
