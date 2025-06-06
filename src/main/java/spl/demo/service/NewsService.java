package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import spl.demo.dto.CardDto;
import spl.demo.dto.NewsDto;
import spl.demo.dto.SummaryNewsDto;
import spl.demo.entity.*;
import spl.demo.repository.NewsRepository;
import spl.demo.repository.StyleSummaryRepository;
import spl.demo.repository.SummaryRepository;
import spl.demo.repository.SignupRepository;
import spl.demo.repository.ScrapRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final SummaryRepository summaryRepository;
    private final StyleSummaryRepository styleSummaryRepository;
    private final GeminiService geminiService;
    private final SignupRepository signupRepository;
    private final ScrapRepository scrapRepository;

    public void saveNewsIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());

            String cleanedContent = dto.getContent().replace("\"", "");
            newsEntity.setContent(cleanedContent);

            newsEntity.setCategory(dto.getCategory());
            newsEntity.setImageUrl(dto.getImageUrl());
            newsEntity.setCreatedAt(LocalDateTime.now());

            newsRepository.save(newsEntity);
        }
    }

    public List<NewsEntity> getAllNews() {
        return newsRepository.findAll();
    }

    public List<NewsEntity> getNewsByCategory(InterestCategoryEntity category) {
        return newsRepository.findByCategory(category);
    }

    public List<CardDto> getCardNewsByCategory(InterestCategoryEntity category) {
        List<NewsEntity> newsList = (category == null)
                ? newsRepository.findAll()
                : newsRepository.findByCategory(category);

        return newsList.stream()
                .map(news -> new CardDto(news.getNewsId(), news.getTitle(), news.getImageUrl()))
                .toList();
    }

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

    @Transactional
    public void summarizeAllNews() {
        List<NewsEntity> newsList = newsRepository.findAll();

        for (NewsEntity news : newsList) {
            try {
                if (!summaryRepository.existsByNews(news)) {
                    String summaryText = callWithRetry(() -> geminiService.summarizeTo4Lines(news.getContent()));
                    summaryRepository.save(new SummaryEntity(news, summaryText));
                }

                for (SummaryStyle style : SummaryStyle.values()) {
                    if (style == SummaryStyle.DEFAULT) continue;

                    boolean exists = styleSummaryRepository
                            .findByNews_IdAndStyle(news.getId(), style)
                            .isPresent();

                    if (!exists) {
                        String styledText = callWithRetry(() -> geminiService.generateStyledSummary(news.getContent(), style));
                        styleSummaryRepository.save(new StyleSummaryEntity(news, styledText, style));
                    }
                }

                Thread.sleep(2000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.err.println("요약 작업 인터럽트: " + news.getId());
            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - 뉴스 ID: " + news.getId());
                e.printStackTrace();
            }
        }
    }

    private String callWithRetry(Supplier<String> request) throws Exception {
        int retries = 0;
        while (true) {
            try {
                return request.get();
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (retries++ >= 3) throw e;

                long retryDelay = 60_000;
                System.err.println("429 오류 발생, " + retryDelay / 1000 + "초 후 재시도 (" + retries + "회차)");
                Thread.sleep(retryDelay);
            }
        }
    }

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
                        .newsId(news.getNewsId())
                        .title(news.getTitle())
                        .url(news.getUrl())
                        .createdAt(news.getCreatedAt())
                        .imageUrl(news.getImageUrl())
                        .summaries(summaryMap.get(news.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public List<SummaryNewsDto> searchNewsDtoByTitle(String keyword) {
        List<NewsEntity> newsList = newsRepository.findByTitleContainingIgnoreCase(keyword);

        return newsList.stream()
                .map(news -> SummaryNewsDto.builder()
                        .id(news.getId())
                        .newsId(news.getNewsId())
                        .title(news.getTitle())
                        .url(news.getUrl())
                        .createdAt(news.getCreatedAt())
                        .imageUrl(news.getImageUrl())
                        .summaries(null)
                        .build())
                .toList();
    }

    // ✅ 사용자 기준 스크랩 여부 포함 뉴스 목록
    public List<NewsDto> getAllNewsWithScrapStatus(String userId) {
        SignupEntity user = signupRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);
        Set<Long> scrappedNewsIds = scraps.stream()
                .map(s -> s.getNews().getId())
                .collect(Collectors.toSet());

        return newsRepository.findAll().stream()
                .map(news -> {
                    boolean scrapped = scrappedNewsIds.contains(news.getId());

                    NewsDto dto = new NewsDto();
                    dto.setNewsId(news.getNewsId());
                    dto.setTitle(news.getTitle());
                    dto.setUrl(news.getUrl());               // 필요하면 포함
                    dto.setContent(null);                    // 필요 없으면 null 처리
                    dto.setImageUrl(news.getImageUrl());
                    dto.setCategory(news.getCategory());     // 필요 없으면 생략 가능
                    dto.setScrapped(scrapped);               // boolean 필드니까 isScrapped가 아닌 setScrapped로 설정

                    return dto;
                })
                .toList();
    }

}