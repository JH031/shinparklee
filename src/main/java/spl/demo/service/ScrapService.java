package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spl.demo.dto.ScrapSummaryDto;
import spl.demo.entity.*;
import spl.demo.repository.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final SignupRepository signupRepository;
    private final NewsRepository newsRepository;
    private final ScrapRepository scrapRepository;
    private final SummaryRepository summaryRepository;
    private final StyleSummaryRepository styleSummaryRepository;

    public void scrapNews(Long userId, String newsId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        NewsEntity news = newsRepository.findByNewsId(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스 없음"));

        if (!scrapRepository.existsByUserAndNews(user, news)) {
            ScrapEntity scrap = new ScrapEntity();
            scrap.setUser(user);
            scrap.setNews(news);
            scrapRepository.save(scrap);
        }
    }

    public void cancelScrap(Long userId, String newsId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        NewsEntity news = newsRepository.findByNewsId(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스 없음"));

        scrapRepository.findByUserAndNews(user, news).ifPresent(scrapRepository::delete);
    }

    public List<ScrapSummaryDto> getScrappedNewsWithSummaries(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        return scraps.stream().map(scrap -> {
            NewsEntity news = scrap.getNews();

            // 기본 요약문
            String defaultSummary = summaryRepository.findByNews(news)
                    .map(SummaryEntity::getSummaryText)
                    .orElse("요약문 없음");

            // 말투 요약문
            EnumMap<SummaryStyle, String> summaries = new EnumMap<>(SummaryStyle.class);
            summaries.put(SummaryStyle.DEFAULT, defaultSummary);

            for (SummaryStyle style : SummaryStyle.values()) {
                if (style == SummaryStyle.DEFAULT) continue;

                styleSummaryRepository.findByNews_IdAndStyle(news.getId(), style);
            }

            return new ScrapSummaryDto(news.getId(), news.getTitle(), news.getUrl(), summaries);
        }).toList();
    }

    public List<NewsEntity> getScrappedNews(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return scrapRepository.findByUser(user).stream()
                .map(ScrapEntity::getNews)
                .toList();
    }
}

