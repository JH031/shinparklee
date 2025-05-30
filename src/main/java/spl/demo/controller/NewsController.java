package spl.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.crawler.NaverNewsCrawler;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.NewsEntity;
import spl.demo.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*") // Postman 테스트 허용
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // ✅ 크롤링 실행 (sid → enum 변환 후 실행)
    @PostMapping("/crawl/{sid}")
    public ResponseEntity<String> crawlAndSave(@PathVariable("sid") String sid) {
        InterestCategoryEntity category = switch (sid) {
            case "100" -> InterestCategoryEntity.Politics;
            case "101" -> InterestCategoryEntity.Economy;
            case "102" -> InterestCategoryEntity.Society;
            case "103" -> InterestCategoryEntity.LifestyleCulture;
            case "104" -> InterestCategoryEntity.Entertainment;
            case "105" -> InterestCategoryEntity.ITScience;
            default -> throw new IllegalArgumentException("Unknown SID: " + sid);
        };

        NaverNewsCrawler.crawlCategoryNews(sid, category, newsService);
        return ResponseEntity.ok("크롤링 및 저장 완료: " + category.name());
    }

    // ✅ 뉴스 목록 조회 (전체 또는 enum 카테고리 필터링)
    @GetMapping
    public ResponseEntity<List<NewsEntity>> getNews(
            @RequestParam(name = "category", required = false) InterestCategoryEntity category
    ) {
        if (category == null) {
            return ResponseEntity.ok(newsService.getAllNews());
        } else {
            return ResponseEntity.ok(newsService.getNewsByCategory(category));
        }
    }
}
