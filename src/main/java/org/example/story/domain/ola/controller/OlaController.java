package org.example.story.domain.ola.controller;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.ola.record.request.OlaRequest;
import org.example.story.domain.ola.record.response.OlaListResponse;
import org.example.story.domain.ola.record.response.OlaResponse;
import org.example.story.domain.ola.service.OlaService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ola")
public class OlaController {
    private final OlaService olaService;

    @PostMapping("/{portfolio_id}")
    public CompletableFuture<OlaResponse> feed(@PathVariable("portfolio_id") Long portfolio_id,
                                               @RequestBody OlaRequest request) {
        return olaService.feedOla(request.question(), portfolio_id);
    }

    @GetMapping("/history/{portfolio_id}")
    public OlaListResponse history(@PathVariable("portfolio_id") Long portfolio_id){
        return olaService.historyOla(portfolio_id);
    }
}
