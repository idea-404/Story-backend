package org.example.story.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.profile.record.response.ViewProfileResDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewProfileService {
    private final QueryProfileInformService queryProfileInformService;

    public ViewProfileResDto execute(Long userId) {
        return queryProfileInformService.execute(userId);
    }
}
