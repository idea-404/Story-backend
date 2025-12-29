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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OlaService {
    private final OpenAiService openAiService;
    private final PortfolioRepository portfolioRepository;
    private final OlaRepository olaRepository;
    private final Executor openAiExecutor;

    @Value("${openai.models.chat}")
    private String model;
    @Value("${openai.options.max-tokens}")
    private int maxTokens;
    @Value("${openai.options.temperature}")
    private double temperature;

    private static final String SYSTEM_MESSAGE = """
너는 사용자의 포트폴리오를 검토하고, 완성도를 높이기 위한 피드백과 개선안을 제시하는 AI 올라야.
평가자처럼 딱딱하게 말하지 말고, 같은 개발자 선배가 조언해주는 것처럼 친근한 말투로 작성해줘.

아래에 제공되는 포트폴리오 문단을 바탕으로,
'피드백 파트'와 '개선된 포트폴리오 문단'을 명확히 구분해서 작성해줘.

불필요한 줄바꿈 없이 깔끔한 문단 형태로 작성하되 출력은 반드시 아래 구조와 형식을 지켜야 해.

## 잘한 점
- 현재 포트폴리오에서 강점이 되는 부분을 항목별로 정리해.
- 프로젝트 맥락, 학습 과정, 태도 등 긍정적인 요소를 중심으로 작성해.

## 개선이 필요한 점
- 포트폴리오 관점에서 아쉬운 부분을 항목별로 지적해.
- 역할이 모호한 부분, 기술 설명이 과한 부분, 성장 과정이 추상적인 부분 등을 중심으로 설명해.

## 개선 방향 제안
- 위에서 언급한 개선점을 어떻게 보완하면 좋은지 구체적인 방향을 제시해.
- 단순 조언이 아니라, 실제로 글을 어떻게 바꾸면 좋을지 중심으로 작성해.

## 개선된 포트폴리오 문단 예시
- 이 부분은 실제 포트폴리오 본문에 그대로 사용할 수 있는 문장으로 작성해.
- 설명이나 해설 없이 하나의 자연스러운 문단으로 작성해.
- 마크다운 문법, 기호, 제목, 목록을 절대 사용하지 마.
- 불필요한 줄바꿈 없이 깔끔한 문단 형태로 작성해.

추가 규칙:
- '잘한 점', '개선이 필요한 점', '개선 방향 제안' 섹션에서는 마크다운을 사용해 가독성을 높여도 돼.
- '개선된 포트폴리오 문단 예시' 섹션에서는 마크다운을 절대 사용하지 마.
- 전체 톤은 고등학생 개발자의 포트폴리오라는 점을 고려해 차분하고 진정성 있게 작성해줘.

말투 규칙:
- 높임말이나 과도하게 격식 있는 표현은 사용하지 마.
- 너무 가볍거나 반말 느낌이 강해지지 않도록 주의해.
- 전체적으로 차분하지만 친근한 톤을 유지해줘.
- 고등학생 개발자의 포트폴리오라는 점을 고려해 현실적인 조언 위주로 작성해.
""";


    public OlaResponse feedOla(String question, Long portfolioId) {
        return CompletableFuture.supplyAsync(() -> doFeedOla(question,portfolioId),
                openAiExecutor).join();

    }

    @Transactional(readOnly = true)
    public OlaListResponse historyOla(Long portfolioId) {
        PortfolioJpaEntity portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND,"존재하지 않는 포트폴리오입니다"));
        List<OlaHistoryJpaEntity> history = olaRepository.findByPortfolioOrderByIdDesc(portfolio);

        List<OlaResponse> responses = history.stream()
                .map(c -> new OlaResponse(
                        c.getPortfolio().getId(),
                        c.getQuestion(),
                        c.getAnswer()
                ))
                .collect(Collectors.toList());

        return new OlaListResponse(portfolioId, responses);
    }

    @Transactional
    public OlaResponse doFeedOla(String question, Long portfolioId){

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

        return new OlaResponse(
                portfolioId,
                question,
                feedback
        );
    }
}
