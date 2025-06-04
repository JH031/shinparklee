package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spl.demo.crawler.HotTopicCrawler;
import spl.demo.service.NewsService;

@RestController
@RequestMapping("/api/crawler") // 뉴스 크롤링 관련 api 기본 경로
@RequiredArgsConstructor
public class NewsCrawlingController {

    private final NewsService newsService;

    @Operation(summary = "핫토픽 뉴스 생성하기")
    @PostMapping("/hot-news")
    public String crawlHotNews() {
        HotTopicCrawler.crawlYonhapHotTopics(newsService);  // ✅ HotNewsService 제거됨
        return "✅ 연합뉴스 핫토픽 크롤링 완료";
    }
}

