package org.example.story.domain.ola.service;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.example.story.domain.ola.entity.OlaHistoryJpaEntity;
import org.example.story.domain.ola.record.response.OlaListResponse;
import org.example.story.domain.ola.record.response.OlaResponse;
import org.example.story.domain.ola.repository.OlaRepository;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.repository.PortfolioRepository;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OlaService {
    private final OpenAiService openAiService;
    private final PortfolioRepository portfolioRepository;
    private final OlaRepository olaRepository;

    @Value("${openai.models}")
    private String model;
    @Value("${openai.options.max-tokens}")
    private int maxTokens;
    @Value("${openai.options.temperature}")
    private double temperature;

    private static final String SYSTEM_MESSAGE = """
    너는 사용자의 포트폴리오를 확인하고 피드백을 주는 ai 이름은 올라야. \
    아래 사용자가 작성한 포트폴리오의 일부를 보고 이상적인 포트폴리오 되기위한 피드백을 제공해줘. \
    피드백의 형식은 조언과 개선안의 예시를 보여주는 형식으로 부탁해\
    """;

    @Transactional
    public OlaResponse feedOla(String question, Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"존재하지 않는 포트폴리오입니다"));


        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(
                        new ChatMessage("system", SYSTEM_MESSAGE),
                        new ChatMessage("user", question)
                ))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        var response = openAiService.createChatCompletion(request);
        var choices = response.getChoices();
        if (choices == null || choices.isEmpty()) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "AI로부터 응답을 받지 못했습니다.");
        }
        String feedback = choices.get(0).getMessage().getContent();

        OlaHistoryJpaEntity saved = OlaHistoryJpaEntity.builder()
                .portfolio(portfolio)
                .question(question)
                .answer(feedback)
                .build();
        olaRepository.save(saved);

        return new OlaResponse(
                saved.getId(),
                saved.getPortfolio().getId(),
                saved.getQuestion(),
                feedback
        );
    }

    @Transactional(readOnly = true)
    public OlaListResponse historyOla(Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"존재하지 않는 포트폴리오입니다"));
        List<OlaHistoryJpaEntity> history = olaRepository.findByPortfolioOrderByIdDesc(portfolio);

        List<OlaResponse> responses = history.stream()
                .map(c -> new OlaResponse(
                        c.getId(),
                        c.getPortfolio().getId(),
                        c.getQuestion(),
                        c.getAnswer()
                ))
                .collect(Collectors.toList());

        return new OlaListResponse(portfolioId, responses);
    }
}
