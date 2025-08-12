package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.StyleSummaryEntity;
import spl.demo.entity.SummaryStyle;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.service.GeminiService;

@RestController
@RequestMapping("/api/v1/summarize")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;
    private final NewsRepository newsRepository;
    private final StyleSummaryRepository styleSummaryRepository;

    // ✅ 말투 요약용 엔드포인트
    @Operation(summary = "말투 스타일받아서 요약")
    @PostMapping("/style")
    public ResponseEntity<String> summarizeWithStyle(
            @RequestParam(name = "newsId") Long newsId,
            @RequestParam(name = "style", required = false, defaultValue = "DEFAULT") SummaryStyle style
    ) {
        // 1. 뉴스 ID로 뉴스 엔티티 조회
        NewsEntity news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스가 존재하지 않습니다."));

        // 2. Gemini API로 스타일 요약 생성
        String summaryText = geminiService.generateStyledSummary(news.getContent(), style);

        // 3. 기존 요약이 있으면 갱신, 없으면 새로 저장
        styleSummaryRepository.findByNews_IdAndStyle(newsId, style)
                .ifPresentOrElse(existing -> {
                    existing.updateSummaryText(summaryText);
                    styleSummaryRepository.save(existing);
                }, () -> {
                    StyleSummaryEntity summary = new StyleSummaryEntity(news, summaryText, style);
                    styleSummaryRepository.save(summary);
                });

        // 4. 클라이언트에 요약 결과 반환
        return ResponseEntity.ok(summaryText);
    }
}
