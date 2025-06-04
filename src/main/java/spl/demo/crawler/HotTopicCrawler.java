package spl.demo.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import spl.demo.dto.NewsDto;
import spl.demo.service.NewsService;

import java.util.HashSet;
import java.util.Set;

public class HotTopicCrawler {

    public static void crawlYonhapHotTopics(NewsService newsService) {
        try {
            String url = "https://news.naver.com/main/ranking/popularDay.naver";

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            // “001”(연합뉴스) 기사 링크만 골라내기
            Elements articleLinks = doc.select("a[href^=https://n.news.naver.com/article/001/]");
            System.out.println("🔍 전체 연합뉴스 기사 링크 수: " + articleLinks.size());

            Set<String> seenLinks = new HashSet<>();
            int savedCount = 0;
            int limit = 5;

            for (Element linkEl : articleLinks) {
                String link = linkEl.absUrl("href");
                if (!seenLinks.add(link)) continue;  // 중복 제거
                if (savedCount >= limit) break;

                Document detailDoc = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .get();

                // 제목
                Element titleEl = detailDoc.selectFirst("h2.media_end_head_headline span");

                // 본문(article#dic_area) 가져오기
                Element contentEl = detailDoc.selectFirst("article#dic_area");
                String contentText = "";
                if (contentEl != null) {
                    // 이미지 캡션(span.end_photo_org) 부분 제거
                    contentEl.select("span.end_photo_org").remove();
                    contentText = contentEl.text().trim();
                }

                // 대표 이미지:
                // 1) 일반적으로 <div class="nbd_im_w"> 안의 <img> 태그
                // 2) 혹시 구조가 다르면 “newsct_article” 내 모든 img 태그 중 첫 번째
                Element imageEl = detailDoc.selectFirst("div.nbd_im_w img, div#newsct_article img");
                String imageUrl = "";
                if (imageEl != null) {
                    imageUrl = imageEl.attr("src");
                }

                if (titleEl == null || contentText.isBlank()) {
                    System.out.println("❌ 제목 또는 본문 없음. 건너뜀: " + link);
                    continue;
                }

                String newsId = generateNewsId(link);
                System.out.println("🆔 newsId: " + newsId);
                System.out.println("📰 제목: " + titleEl.text());
                System.out.println("📄 본문(앞 30자): " +
                        (contentText.length() > 30 ? contentText.substring(0, 30) + "..." : contentText));
                System.out.println("🖼 이미지: " + (imageUrl.isEmpty() ? "(없음)" : imageUrl));

                NewsDto dto = new NewsDto();
                dto.setNewsId(newsId);
                dto.setTitle(titleEl.text());
                dto.setContent(contentText);
                dto.setImageUrl(imageUrl);
                dto.setUrl(link);
                dto.setCategory(null);

                System.out.println("✅ 저장 시도: " + dto.getTitle());
                newsService.saveHotTopicIfNotExists(dto);
                savedCount++;
            }

            System.out.println("🎉 연합뉴스 핫토픽 크롤링 완료, 저장된 뉴스 수: " + savedCount);

        } catch (Exception e) {
            System.out.println("❌ 크롤링 중 오류 발생:");
            e.printStackTrace();
        }
    }

    /**
     * URL 예시: https://n.news.naver.com/article/001/0015429378?ntype=RANKING
     * split 후 parts 배열은 ["https:", "", "n.news.naver.com", "article", "001", "0015429378?ntype=RANKING"]
     * 따라서 length는 6이므로 parts.length >= 6 으로 체크해야 합니다.
     */
    private static String generateNewsId(String link) {
        try {
            String[] parts = link.split("/");
            if (parts.length >= 6) {
                String pressId = parts[4];              // "001"
                String articleWithQuery = parts[5];     // "0015429378?ntype=RANKING"
                String articleId = articleWithQuery.split("\\?")[0]; // "0015429378"
                return pressId + "_" + articleId;       // 예: "001_0015429378"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown_id";
    }
}
