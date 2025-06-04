package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "카테고리별 크롤링")
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
    @Operation(summary = "모든 카테고리 크롤링")
    @PostMapping("/crawl/all")
    public ResponseEntity<String> crawlAllCategories() {
        StringBuilder result = new StringBuilder();

        // 각 SID에 해당하는 카테고리 매핑
        String[][] sidCategoryPairs = {
                {"100", "Politics"},
                {"101", "Economy"},
                {"102", "Society"},
                {"103", "LifestyleCulture"},
                {"104", "Entertainment"},
                {"105", "ITScience"}
        };

        for (String[] pair : sidCategoryPairs) {
            String sid = pair[0];
            InterestCategoryEntity category = InterestCategoryEntity.valueOf(pair[1]);
            try {
                NaverNewsCrawler.crawlCategoryNews(sid, category, newsService);
                result.append("✅ ").append(category.name()).append(" 크롤링 완료\n");
            } catch (Exception e) {
                result.append("❌ ").append(category.name()).append(" 실패\n");
            }
        }

        return ResponseEntity.ok(result.toString());
    }


    @Operation(summary = "뉴스 목록 조회")
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

    @Operation(summary = "모든 뉴스 제목만 조회")
    @GetMapping("/titles")
    public ResponseEntity<List<String>> getAllNewsTitles() {
        List<String> titles = newsService.getAllNews()
                .stream()
                .map(NewsEntity::getTitle)
                .toList();

        return ResponseEntity.ok(titles);
    }

}
