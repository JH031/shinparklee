package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spl.demo.dto.NewsDto;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.SummaryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final SummaryRepository summaryRepository;
    private final GeminiService geminiService;

    // ✅ 뉴스 저장 (중복 방지 + 큰따옴표 제거)
    public void saveNewsIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());

            // ✅ 큰따옴표 제거
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

    // ✅ Gemini를 활용한 뉴스 요약 → Summary DB 저장
    @Transactional
    public void summarizeAllNews() {
        List<NewsEntity> newsList = newsRepository.findAll();

        for (NewsEntity news : newsList) {
            try {
                if (!summaryRepository.existsByNews(news)) {
                    String summaryText = geminiService.summarizeTo4Lines(news.getContent());

                    // 생성자 기반 저장으로 최적화 ✅
                    SummaryEntity summary = new SummaryEntity(news, summaryText);
                    summaryRepository.save(summary);
                }
            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - 뉴스 ID: " + news.getId());
                e.printStackTrace();
            }
        }
    }
}
