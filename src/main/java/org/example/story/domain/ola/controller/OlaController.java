package org.example.story.domain.ola.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.ola.record.request.OlaRequest;
import org.example.story.domain.ola.record.response.OlaListResponse;
import org.example.story.domain.ola.record.response.OlaResponse;
import org.example.story.domain.ola.service.OlaService;
import org.example.story.global.security.auth.AuthUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ola")
public class OlaController {
    private final OlaService olaService;
    private final AuthUtils authUtils;

    @PostMapping("/{portfolioId}")
    public OlaResponse feed(@PathVariable("portfolioId") Long portfolioId,
                                               @RequestBody OlaRequest request) {
        Long userId = authUtils.getCurrentUserId();
        return olaService.feedOla(userId,request.question(), portfolioId);
    }

    @GetMapping("/history/{portfolioId}")
    public OlaListResponse history(@PathVariable("portfolioId") Long portfolioId){
        return olaService.historyOla(portfolioId);
    }
}
