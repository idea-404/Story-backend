package org.example.story.domain.user.record.common.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccount(
        @JsonProperty("email") String email
) {
}
