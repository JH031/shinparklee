package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spl.demo.dto.NewsDto;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.StyleSummaryEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.entity.SummaryStyle;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.repository.SummaryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final SummaryRepository summaryRepository;
    private final StyleSummaryRepository styleSummaryRepository;
    private final GeminiService geminiService;

    // 뉴스 저장 (중복 방지 + 큰따옴표 제거)
    public void saveNewsIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());

            // 큰따옴표 제거
            String cleanedContent = dto.getContent().replace("\"", "");
            newsEntity.setContent(cleanedContent);

            newsEntity.setCategory(dto.getCategory());

            newsRepository.save(newsEntity);
        }
    }

    // ✅ 전체 뉴스 조회
    public List<NewsEntity> getAllNews() {
        return newsRepository.findAll();
    }

    // ✅ 카테고리별 뉴스 조회
    public List<NewsEntity> getNewsByCategory(InterestCategoryEntity category) {
        return newsRepository.findByCategory(category);
    }

    // ✅ Gemini를 활용한 뉴스 요약 → 기본 요약 + 말투 요약 모두 저장
    @Transactional
    public void summarizeAllNews() {
        List<NewsEntity> newsList = newsRepository.findAll();

        for (NewsEntity news : newsList) {
            try {
                //  기본 요약 저장
                if (!summaryRepository.existsByNews(news)) {
                    String summaryText = geminiService.summarizeTo4Lines(news.getContent());
                    SummaryEntity summary = new SummaryEntity(news, summaryText);
                    summaryRepository.save(summary);
                }

                //  말투 요약 저장 (모든 말투)
                for (SummaryStyle style : SummaryStyle.values()) {
                    if (style == SummaryStyle.DEFAULT) continue;

                    boolean exists = styleSummaryRepository.findByNewsIdAndStyle(news.getId(), style).isPresent();
                    if (!exists) {
                        String styled = geminiService.generateStyledSummary(news.getContent(), style);
                        StyleSummaryEntity styledSummary = new StyleSummaryEntity(news, styled, style);
                        styleSummaryRepository.save(styledSummary);
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - 뉴스 ID: " + news.getId());
                e.printStackTrace();
            }
        }
    }
    public void saveHotTopicIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());
            newsEntity.setContent(dto.getContent().replace("\"", ""));
            newsEntity.setCategory(InterestCategoryEntity.HOT_TOPIC);      // 핫토픽은 카테고리 없이
            newsEntity.setHotTopic(true);       // ✅ 핫토픽 표시

            newsRepository.save(newsEntity);
        }
    }

}
