package org.example.story.domain.profile.record.response;

import org.example.story.domain.profile.record.common.BlogCommonDto;
import org.example.story.domain.profile.record.common.PortfolioCommonDto;

import java.util.List;

public record ViewProfileResDto(
        String nickname,
        String studentId,
        String introduce,
        String profileImage,
        List<PortfolioCommonDto> portfolio,
        List<BlogCommonDto> blog
) {
}
