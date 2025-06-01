package spl.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.SummaryRepository;
import spl.demo.service.GeminiService;
import spl.demo.service.NewsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class SummaryController {

    private final NewsService newsService;
    private final NewsRepository newsRepository; // ✅ 추가
    private final SummaryRepository summaryRepository; // ✅ 추가
    private final GeminiService geminiService; // ✅ 추가

    @PostMapping("/generate/all")
    public ResponseEntity<String> summarizeAllNews() {
        newsService.summarizeAllNews();
        return ResponseEntity.ok("모든 뉴스 요약 완료");
    }



}
