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
            String url = "https://news.naver.com/main/ranking/popularDay.naver?mid=etc&sid1=111"; // 뉴스홈 기본 랭킹 페이지

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            // 한국일보 박스 탐색
            Element pressBox = doc.selectFirst("a[href*='/press/469/']") // 한국일보 언론사 링크 찾기
                    .closest("div.rankingnews_box");

            if (pressBox == null) {
                System.out.println("❌ 한국일보 박스를 찾을 수 없습니다.");
                return;
            }

            Elements items = pressBox.select("ul.rankingnews_list > li");
            System.out.println("📰 한국일보 랭킹 기사 수: " + items.size());

            Set<String> seenLinks = new HashSet<>();
            int savedCount = 0;
            int limit = 5;

            for (Element item : items) {
                if (savedCount >= limit) break;

                Element linkEl = item.selectFirst("a.list_title");
                if (linkEl == null) continue;

                String link = linkEl.absUrl("href");
                if (!seenLinks.add(link)) continue;

                Document detailDoc = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .get();

                // 제목
                Element titleEl = detailDoc.selectFirst("h2.media_end_head_headline span");

                // 본문 내용
                Element contentEl = detailDoc.selectFirst("article#dic_area");
                String contentText = "";
                if (contentEl != null) {
                    contentEl.select("span.end_photo_org").remove();  // 불필요한 캡션 제거
                    contentText = contentEl.text().trim();
                }

                Element imageMeta = detailDoc.selectFirst("meta[property=og:image]");
                String imageUrl = null;
                if (imageMeta != null) {
                    imageUrl = imageMeta.attr("content");
                }

                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    System.out.println("🚫 본문 이미지 없음 → 제외: " + link);
                    continue;
                }

                String newsId = generateNewsId(link);
                System.out.println("🆔 newsId: " + newsId);
                System.out.println("📰 제목: " + (titleEl != null ? titleEl.text() : "(제목 없음)"));
                System.out.println("🖼 이미지: " + imageUrl);

                NewsDto dto = new NewsDto();
                dto.setNewsId(newsId);
                dto.setTitle(titleEl != null ? titleEl.text() : "");
                dto.setContent(contentText);
                dto.setImageUrl(imageUrl);
                dto.setUrl(link);
                dto.setCategory(null);

                newsService.saveHotTopicIfNotExists(dto);
                savedCount++;
            }

            System.out.println("🎉 한국일보 본문 이미지 기준 크롤링 완료, 저장된 뉴스 수: " + savedCount);

        } catch (Exception e) {
            System.out.println("❌ 크롤링 중 오류 발생:");
            e.printStackTrace();
        }
    }

    private static String generateNewsId(String link) {
        try {
            String[] parts = link.split("/");
            if (parts.length >= 6) {
                String pressId = parts[4];              // "469"
                String articleWithQuery = parts[5];     // "0000868808?ntype=RANKING"
                String articleId = articleWithQuery.split("\\?")[0]; // "0000868808"
                return pressId + "_" + articleId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown_id";
    }
}
