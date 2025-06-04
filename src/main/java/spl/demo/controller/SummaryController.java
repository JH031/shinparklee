package spl.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.StyleSummaryEntity;
import spl.demo.entity.SummaryStyle;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.repository.SummaryRepository;
import spl.demo.service.GeminiService;
import spl.demo.service.NewsService;
import spl.demo.service.SummaryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class SummaryController {

    private final NewsService newsService;
    private final NewsRepository newsRepository;
    private final SummaryService summaryService;
    private final StyleSummaryRepository styleSummaryRepository;
    private final GeminiService geminiService;

    // ✅ 기본 요약 전체 생성
    @PostMapping("/generate/all")
    public ResponseEntity<String> summarizeAllNews() {
        newsService.summarizeAllNews();
        return ResponseEntity.ok("모든 뉴스 요약 완료");
    }

    // ✅ 말투 요약 전체 생성 (ID 기반으로 수정됨)
    @PostMapping("/generate/all/style")
    public ResponseEntity<String> summarizeAllNewsWithStyle(@RequestParam("style") SummaryStyle style) {
        int savedCount = 0;

        for (NewsEntity news : newsRepository.findAll()) {
            try {
                // ✅ ID 기반으로 조회
                boolean exists = styleSummaryRepository.findByNewsIdAndStyle(news.getId(), style).isPresent();

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

}
