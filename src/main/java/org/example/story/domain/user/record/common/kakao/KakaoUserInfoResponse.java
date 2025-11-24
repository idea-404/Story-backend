package org.example.story.domain.user.record.common.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
}
