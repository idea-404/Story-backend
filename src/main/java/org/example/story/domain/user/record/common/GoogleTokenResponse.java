package org.example.story.domain.user.record.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") Long expiresIn
) {
}
