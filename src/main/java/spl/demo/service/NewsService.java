package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spl.demo.dto.CardDto;
import spl.demo.dto.NewsDto;
import spl.demo.dto.SummaryNewsDto;
import spl.demo.entity.*;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.repository.SummaryRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            newsEntity.setImageUrl(dto.getImageUrl());
            newsEntity.setCreatedAt(LocalDateTime.now());

            newsRepository.save(newsEntity);
        }
    }


    // ✅ 전체 뉴스 조회 (컨트롤러에서 사용)
    public List<NewsEntity> getAllNews() {
        return newsRepository.findAll();
    }

    // ✅ 카테고리별 뉴스 조회 (컨트롤러에서 사용)
    public List<NewsEntity> getNewsByCategory(InterestCategoryEntity category) {
        return newsRepository.findByCategory(category);
    }

    // ✅ 카테고리별 카드 뉴스 조회
    public List<CardDto> getCardNewsByCategory(InterestCategoryEntity category) {
        List<NewsEntity> newsList = (category == null)
                ? newsRepository.findAll()
                : newsRepository.findByCategory(category);

        return newsList.stream()
                .map(news -> new CardDto(news.getNewsId(), news.getTitle(), news.getImageUrl()))
                .toList();
    }

    // ✅ 핫토픽 뉴스 저장
    public void saveHotTopicIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());
            newsEntity.setContent(dto.getContent().replace("\"", ""));
            newsEntity.setCategory(InterestCategoryEntity.HOT_TOPIC);
            newsEntity.setHotTopic(true);
            newsEntity.setImageUrl(dto.getImageUrl());
            newsEntity.setCreatedAt(LocalDateTime.now());

            newsRepository.save(newsEntity);
        }
    }

    // ✅ Gemini 기반 전체 요약 생성 (기본 + 스타일)
    @Transactional
    public void summarizeAllNews() {
        List<NewsEntity> newsList = newsRepository.findAll();

        for (NewsEntity news : newsList) {
            try {
                // 기본 요약
                if (!summaryRepository.existsByNews(news)) {
                    String summaryText = geminiService.summarizeTo4Lines(news.getContent());
                    SummaryEntity summary = new SummaryEntity(news, summaryText);
                    summaryRepository.save(summary);
                }

                // 스타일 요약
                for (SummaryStyle style : SummaryStyle.values()) {
                    if (style == SummaryStyle.DEFAULT) continue;

                    boolean exists = styleSummaryRepository
                            .findByNews_IdAndStyle(news.getId(), style)
                            .isPresent();

                    if (!exists) {
                        String styled = geminiService.generateStyledSummary(news.getContent(), style);
                        StyleSummaryEntity styledSummary = new StyleSummaryEntity(news, styled, style);
                        styleSummaryRepository.save(styledSummary);
                    }
                }

                Thread.sleep(5000); // ✅ 요약 지연 방지

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.err.println("요약 작업이 인터럽트됨: " + news.getId());
            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - 뉴스 ID: " + news.getId());
                e.printStackTrace();
            }
        }
    }

    // ✅ 핫토픽 뉴스 요약 리스트 반환
    public List<SummaryNewsDto> getHotTopicSummariesWithAllStyles() {
        List<NewsEntity> hotNewsList = newsRepository.findByHotTopicTrue();

        List<SummaryEntity> baseSummaries = summaryRepository.findByNewsIn(hotNewsList);
        List<StyleSummaryEntity> styleSummaries = styleSummaryRepository.findByNewsIn(hotNewsList);

        Map<Long, EnumMap<SummaryStyle, String>> summaryMap = new HashMap<>();

        for (SummaryEntity base : baseSummaries) {
            summaryMap
                    .computeIfAbsent(base.getNews().getId(), k -> new EnumMap<>(SummaryStyle.class))
                    .put(SummaryStyle.DEFAULT, base.getSummaryText());
        }

        for (StyleSummaryEntity style : styleSummaries) {
            summaryMap
                    .computeIfAbsent(style.getNews().getId(), k -> new EnumMap<>(SummaryStyle.class))
                    .put(style.getStyle(), style.getSummaryText());
        }

        return hotNewsList.stream()
                .filter(news -> summaryMap.containsKey(news.getId()))
                .map(news -> SummaryNewsDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .url(news.getUrl())
                        .createdAt(news.getCreatedAt())
                        .summaries(summaryMap.get(news.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    // ✅ 뉴스 제목 검색
    public List<SummaryNewsDto> searchNewsDtoByTitle(String keyword) {
        List<NewsEntity> newsList = newsRepository.findByTitleContainingIgnoreCase(keyword);

        return newsList.stream()
                .map(news -> SummaryNewsDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .url(news.getUrl())
                        .createdAt(news.getCreatedAt())
                        .summaries(null)
                        .build())
                .toList();
    }
}
