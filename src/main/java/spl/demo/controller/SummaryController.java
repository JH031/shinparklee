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

    // 기본 요약 + 스타일 요약 전체 생성
    @Operation(summary = "기본 말투+모든 말투로 요약")
    @PostMapping("/generate/all")
    public ResponseEntity<String> summarizeAllNews() {
        newsService.summarizeAllNews();
        return ResponseEntity.ok("모든 뉴스 요약 완료");
    }

    // 핫토픽 요약 조회
    @Operation(summary = "핫토픽 뉴스 조회")
    @GetMapping("/hot")
    public ResponseEntity<List<SummaryNewsDto>> getHotTopicSummarizedNews() {
        return ResponseEntity.ok(newsService.getHotTopicSummariesWithAllStyles());
    }

    // 기본 요약 조회 (newsId + title + 요약)
    @GetMapping("/basic")
    @Operation(summary = "기본 요약 - 제목, 요약, id 조회")
    public ResponseEntity<List<SummaryNewsDto>> getAllBasicSummaries() {
        List<SummaryEntity> summaries = summaryRepository.findAll();
        List<SummaryNewsDto> result = summaries.stream()
                .map(summary -> {
                    EnumMap<SummaryStyle, String> map = new EnumMap<>(SummaryStyle.class);
                    map.put(SummaryStyle.DEFAULT, summary.getSummaryText());

                    return new SummaryNewsDto(
                            summary.getNews().getId(),
                            summary.getNews().getNewsId(),
                            summary.getNews().getTitle(),
                            summary.getNews().getUrl(),
                            summary.getNews().getImageUrl(),
                            null,
                            map
                    );

                })
                .toList();
        return ResponseEntity.ok(result);
    }


    //  스타일 요약 조회 (newsId + title + 스타일 요약)
    @GetMapping("/style")
    @Operation(summary = "스타일 요약 - 제목, 요약, id 조회")
    public ResponseEntity<List<SummaryNewsDto>> getAllStyleSummaries(@RequestParam("style") SummaryStyle style) {
        List<StyleSummaryEntity> summaries = styleSummaryRepository.findByStyle(style);
        List<SummaryNewsDto> result = summaries.stream()
                .map(summary -> {
                    EnumMap<SummaryStyle, String> map = new EnumMap<>(SummaryStyle.class);
                    map.put(style, summary.getSummaryText());

                    return new SummaryNewsDto(
                            summary.getNews().getId(),
                            summary.getNews().getNewsId(),
                            summary.getNews().getTitle(),
                            summary.getNews().getUrl(),
                            summary.getNews().getImageUrl(),
                            null,
                            map
                    );

                })
                .toList();
        return ResponseEntity.ok(result);
    }

}
