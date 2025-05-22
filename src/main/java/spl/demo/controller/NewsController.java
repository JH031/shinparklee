package spl.demo.controller;

import org.springframework.web.bind.annotation.*;
import spl.demo.service.NaverNewsService;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NaverNewsService newsService;

    public NewsController(NaverNewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public String getNews(@RequestParam("query") String query) {
        System.out.println("컨트롤러 들어옴");
        return newsService.searchNews(query);
    }
}
