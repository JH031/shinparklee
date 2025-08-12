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

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements headlineLinks = doc.select("a.sa_text_title[href^=https://n.news.naver.com/mnews/article/]");

            int savedCount = 0;
            int limit = 6;

            for (Element linkEl : headlineLinks) {
                if (savedCount >= limit) break;

                String link = linkEl.attr("href");
                String[] parts = link.split("/");

                if (parts.length < 7) {
                    System.out.println("❌ 잘못된 링크 구조: " + link);
                    continue;
                }

                String pressId = parts[5];
                String articleId = parts[6].split("\\?")[0];
                String newsId = pressId + "_" + articleId;

                Document detailDoc = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0")
                        .get();

                Element titleEl = detailDoc.selectFirst("h2#title_area, h2.media_end_head_headline");
                Element contentEl = detailDoc.selectFirst("#dic_area, #newsct_article");
                Element imageEl = detailDoc.selectFirst("div#newsct_article img, div.article_body img, figure img");

                String imageUrl = null;
                if (imageEl != null) {
                    imageUrl = imageEl.hasAttr("src") ? imageEl.attr("src") : imageEl.attr("data-src");

                    if (imageUrl != null && imageUrl.startsWith("//")) {
                        imageUrl = "https:" + imageUrl;
                    }
                }

                if (imageUrl == null || imageUrl.isBlank()) {
                    System.out.println("🚫 이미지 없음 → 제외: " + link);
                    continue;
                }

                if (titleEl == null || contentEl == null) {
                    System.out.println("❌ 본문 또는 제목 없음: " + link);
                    continue;
                }

                NewsDto dto = new NewsDto();
                dto.setNewsId(newsId);
                dto.setTitle(titleEl.text());
                dto.setUrl(link);
                dto.setContent(contentEl.text());
                dto.setImageUrl(imageUrl);
                dto.setCategory(category);

                newsService.saveNewsIfNotExists(dto);
                System.out.println("✅ 저장 성공: " + dto.getTitle());
                System.out.println("📷 이미지 URL: " + imageUrl);

                savedCount++;
            }

            System.out.println("🎉 크롤링 완료, 저장된 뉴스 개수: " + savedCount);

        } catch (Exception e) {
            System.out.println("❌ 크롤링 중 오류 발생:");
            e.printStackTrace();
        }
    }
}
