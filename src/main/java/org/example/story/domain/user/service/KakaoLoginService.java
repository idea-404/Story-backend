package org.example.story.domain.user.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.TokenResDto;
import org.example.story.domain.user.record.common.kakao.KakaoAccount;
import org.example.story.domain.user.record.common.kakao.KakaoTokenResponse;
import org.example.story.domain.user.record.common.TokenReqDto;
import org.example.story.domain.user.record.common.kakao.KakaoUserInfoResponse;
import org.example.story.global.config.KakaoOAuthConfig;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final RestTemplate restTemplate;
    private final KakaoOAuthConfig kakaoOAuthConfig;
    private final GetAccountTokenService getAccountTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResDto execute(String code) {
        // access_token 받아오기 대작전
        try {
            String accessToken = getKakaoAccessToken(code);
            String email = getKakaoUserEmail(accessToken);
            return getAccountTokenService.execute(email);
        } catch (RestClientException e) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "카카오 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthConfig.getClientId());
        params.add("redirect_uri", kakaoOAuthConfig.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                KakaoTokenResponse.class
        );

        return Optional.ofNullable(response.getBody())
                .map(KakaoTokenResponse::accessToken)
                .orElseThrow(() -> new ExpectedException(
                        HttpStatus.BAD_REQUEST,
                        "카카오 엑세스 토큰을 받지 못했습니다."
                ));
    }

    private String getKakaoUserEmail(String accessToken) {
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                KakaoUserInfoResponse.class
        );

        return Optional.ofNullable(response.getBody())
                .map(KakaoUserInfoResponse::kakaoAccount)
                .map(KakaoAccount::email)
                .orElseThrow(() -> new ExpectedException(
                        HttpStatus.BAD_REQUEST,
                        "카카오 계정에서 이메일 정보를 찾을 수 없습니다."
                ));
    }
}
