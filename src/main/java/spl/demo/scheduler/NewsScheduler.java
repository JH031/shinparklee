////package spl.demo.scheduler;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import spl.demo.crawler.NaverNewsCrawler;
//import spl.demo.entity.InterestCategoryEntity;
//import spl.demo.service.NewsService;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class NewsScheduler {
//
//    private final NewsService newsService;
//
//    // sid1 매핑 (네이버 카테고리 코드)
//    private static final Map<InterestCategoryEntity, String> categorySidMap = new HashMap<>() {{
//        put(InterestCategoryEntity.Politics, "100");
//        put(InterestCategoryEntity.Economy, "101");
//        put(InterestCategoryEntity.Society, "102");
//        put(InterestCategoryEntity.LifestyleCulture, "103");
//        put(InterestCategoryEntity.ITScience, "105");
//        put(InterestCategoryEntity.Entertainment, "104");
//    }};
//
//    public NewsScheduler(NewsService newsService) {
//        this.newsService = newsService;
//    }
//
//    // 매일 0시, 6시, 12시, 18시에 실행
//    @Scheduled(cron = "0 0 0,6,12,18 * * *")
//    public void crawlAllCategoryNews() {
//        System.out.println("스케줄러 실행: 전체 카테고리 뉴스 수집 시작");
//
//        for (Map.Entry<InterestCategoryEntity, String> entry : categorySidMap.entrySet()) {
//            InterestCategoryEntity category = entry.getKey();
//            String sid = entry.getValue();
//            NaverNewsCrawler.crawlCategoryNews(sid, category, newsService);
//        }
//
//        System.out.println("전체 카테고리 뉴스 수집 완료");
//    }
//}