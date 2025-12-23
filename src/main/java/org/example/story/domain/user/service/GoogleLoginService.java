package org.example.story.domain.user.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.story.domain.user.record.common.GoogleTokenResponse;
import org.example.story.domain.user.record.common.TokenReqDto;
import org.example.story.domain.user.record.common.TokenResDto;
import org.example.story.global.config.GoogleOAuthConfig;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginService {
    private final RestTemplate restTemplate;
    private final GoogleOAuthConfig googleOAuthConfig;
    private final GetAccountTokenService getAccountTokenService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public TokenResDto execute(String code) {
        // 토큰 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthConfig.getClientId());
        params.add("client_secret", googleOAuthConfig.getClientSecret());
        params.add("redirect_uri", googleOAuthConfig.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        GoogleTokenResponse tokenResponse = Optional.ofNullable(
                restTemplate.exchange(
                        "https://oauth2.googleapis.com/token",
                        HttpMethod.POST,
                        request,
                        GoogleTokenResponse.class
                ).getBody()
        ).orElseThrow(() ->
                new ExpectedException(HttpStatus.BAD_REQUEST, "구글 OAuth에 문제가 발생하였습니다."));

        // id_token 파싱하기
        String idToken = tokenResponse.idToken();
        if(idToken == null){
            throw new ExpectedException(HttpStatus.BAD_REQUEST, "ID 토큰이 존재하지 않습니다.");
        }

        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new ExpectedException(HttpStatus.BAD_REQUEST, "유효하지 않은 ID 토큰입니다.");
            }

            String email = googleIdToken.getPayload().getEmail();
            if (email == null) {
                throw new ExpectedException(HttpStatus.BAD_REQUEST, "이메일이 존재하지 않습니다.");
            }
            return getAccountTokenService.execute(email);
        } catch (GeneralSecurityException e) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "ID 토큰 검증에 실패하였습니다.");
        } catch (IOException e) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "구글 서버와의 통신에 실패했습니다.");
        } catch (Exception e) {
            log.error("구글 로그인 중 예상치 못한 오류 발생", e);
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생하였습니다");
        }
    }
}
