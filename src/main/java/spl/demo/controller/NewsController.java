package spl.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.crawler.NaverNewsCrawler;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.service.NewsService;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/crawl/{sid}")
    public ResponseEntity<String> crawlAndSave(@PathVariable("sid") String sid) {
        InterestCategoryEntity category = switch (sid) {
            case "100" -> InterestCategoryEntity.Politics;
            case "101" -> InterestCategoryEntity.Economy;
            case "102" -> InterestCategoryEntity.Society;
            case "103" -> InterestCategoryEntity.LifestyleCulture;
            case "105" -> InterestCategoryEntity.ITScience;
            case "106" -> InterestCategoryEntity.Entertainment;
            default -> throw new IllegalArgumentException("Unknown SID: " + sid);
        };

        NaverNewsCrawler.crawlCategoryNews(sid, category, newsService);
        return ResponseEntity.ok("크롤링 및 저장 완료");
    }
}
