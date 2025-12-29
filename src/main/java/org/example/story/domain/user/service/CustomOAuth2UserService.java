package org.example.story.domain.user.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String provider =
                request.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = new HashMap<>();

        if ("google".equals(provider)) {
            attributes.put("email", oAuth2User.getAttribute("email"));
        }

        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount =
                    (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");

            attributes.put("email", kakaoAccount.get("email"));
        }

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
        );
    }
}

