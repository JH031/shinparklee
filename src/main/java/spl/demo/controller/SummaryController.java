package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.SummaryNewsDto;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.StyleSummaryEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.entity.SummaryStyle;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.repository.SummaryRepository;
import spl.demo.service.GeminiService;
import spl.demo.service.NewsService;
import java.util.EnumMap;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class SummaryController {

    private final NewsService newsService;
    private final NewsRepository newsRepository;
    private final StyleSummaryRepository styleSummaryRepository;
    private final GeminiService geminiService;
    private final SummaryRepository summaryRepository;


    // ✅ 기본 요약 전체 생성
    @Operation(summary = "기본 말투로 요약")
    @PostMapping("/generate/all")
    public ResponseEntity<String> summarizeAllNews() {
        newsService.summarizeAllNews();
        return ResponseEntity.ok("모든 뉴스 요약 완료");
    }

    // ✅ 말투 요약 전체 생성 (ID 기반으로 수정됨)
    @Operation(summary = "지정 말투로 요약")
    @PostMapping("/generate/all/style")
    public ResponseEntity<String> summarizeAllNewsWithStyle(@RequestParam("style") SummaryStyle style) {
        int savedCount = 0;

        for (NewsEntity news : newsRepository.findAll()) {
            try {
                // ✅ ID 기반으로 조회
                boolean exists = styleSummaryRepository.findByNews_IdAndStyle(news.getId(), style).isPresent();

                if (exists) {
                    System.out.println("✅ 이미 존재함 - newsId=" + news.getId() + ", style=" + style);
                    continue;
                }

                String summaryText = geminiService.generateStyledSummary(news.getContent(), style);
                StyleSummaryEntity summary = new StyleSummaryEntity(news, summaryText, style);
                styleSummaryRepository.save(summary);

                savedCount++;
            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - newsId=" + news.getId());
            }
        }

        return ResponseEntity.ok("총 " + savedCount + "건 신규 요약 (" + style + ") 저장 완료!");
    }
    @Operation(summary = "핫토픽 뉴스 조회")
    @GetMapping("/hot")
    public ResponseEntity<List<SummaryNewsDto>> getHotTopicSummarizedNews() {
        return ResponseEntity.ok(newsService.getHotTopicSummariesWithAllStyles());
    }

    @GetMapping("/basic")
    @Operation(summary = "기본 요약 - 뉴스 제목과 요약만 조회")
    public ResponseEntity<List<SummaryNewsDto>> getAllBasicSummaries() {
        List<SummaryEntity> summaries = summaryRepository.findAll();
        List<SummaryNewsDto> result = summaries.stream()
                .map(summary -> {
                    EnumMap<SummaryStyle, String> map = new EnumMap<>(SummaryStyle.class);
                    map.put(SummaryStyle.DEFAULT, summary.getSummaryText());

                    return new SummaryNewsDto(
                            null,
                            summary.getNews().getTitle(),
                            null,
                            null,
                            map
                    );
                })
                .toList();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/style")
    @Operation(summary = "스타일 요약 - 뉴스 제목과 요약만 조회")
    public ResponseEntity<List<SummaryNewsDto>> getAllStyleSummaries(@RequestParam("style") SummaryStyle style) {
        List<StyleSummaryEntity> summaries = styleSummaryRepository.findByStyle(style);
        List<SummaryNewsDto> result = summaries.stream()
                .map(summary -> {
                    EnumMap<SummaryStyle, String> map = new EnumMap<>(SummaryStyle.class);
                    map.put(style, summary.getSummaryText());

                    return new SummaryNewsDto(
                            null,
                            summary.getNews().getTitle(),
                            null,
                            null,
                            map
                    );
                })
                .toList();
        return ResponseEntity.ok(result);
    }




}
