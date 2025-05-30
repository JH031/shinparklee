package spl.demo.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import spl.demo.dto.NewsDto;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.service.NewsService;

public class NaverNewsCrawler {

    public static void crawlCategoryNews(String sid, InterestCategoryEntity category, NewsService newsService) {
        try {
            String url = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=" + sid;
            System.out.println("🧭 크롤링 시작: " + url);
            Document doc = Jsoup.connect(url).get();

            Elements headlineLinks = doc.select("a[href^=https://n.news.naver.com/mnews/article/]");

            int savedCount = 0;
            int limit = 10; // ✅ 최대 10개로 제한

            for (Element linkEl : headlineLinks) {
                if (savedCount >= limit) break; // ✅ 10개 저장되면 종료

                String link = linkEl.attr("href");

                // newsId 생성: article/언론사ID/기사ID
                String[] parts = link.split("/");
                if (parts.length < 7) {
                    System.out.println("❌ 잘못된 링크 구조: " + link);
                    continue;
                }
                String pressId = parts[5];
                String articleId = parts[6].split("\\?")[0];
                String newsId = pressId + "_" + articleId;

                // 상세 페이지에서 본문 가져오기
                Document detailDoc = Jsoup.connect(link).get();
                Element contentEl = detailDoc.selectFirst("#dic_area");
                Element titleEl = detailDoc.selectFirst("h2#title_area");

                if (contentEl == null || titleEl == null) {
                    System.out.println("❌ 본문 또는 제목 없음: " + link);
                    continue;
                }

                NewsDto dto = new NewsDto();
                dto.setNewsId(newsId);
                dto.setTitle(titleEl.text());
                dto.setUrl(link);
                dto.setContent(contentEl.text());
                dto.setCategory(category);

                newsService.saveNewsIfNotExists(dto);
                System.out.println("✅ 저장 성공: " + dto.getTitle());
                savedCount++;
            }

            System.out.println("🎉 크롤링 완료, 저장된 뉴스 개수: " + savedCount);

        } catch (Exception e) {
            System.out.println("❌ 크롤링 중 오류 발생:");
            e.printStackTrace();
        }
    }
}
