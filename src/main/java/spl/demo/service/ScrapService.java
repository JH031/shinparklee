package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spl.demo.dto.ScrapSummaryDto;
import spl.demo.dto.StyleSummaryDto;
import spl.demo.entity.*;
import spl.demo.repository.*;

import java.util.List;
import java.util.stream.Collectors;

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

            // 기본 요약만 조회
            String defaultSummary = summaryRepository.findByNews(news)
                    .map(SummaryEntity::getSummaryText)
                    .orElse("요약문 없음");

            // 오직 DEFAULT만 담음
            List<StyleSummaryDto> defaultOnly = List.of(
                    new StyleSummaryDto(news.getId(), SummaryStyle.DEFAULT, defaultSummary)
            );

            return new ScrapSummaryDto(
                    news.getId(),
                    news.getTitle(),
                    news.getUrl(),
                    defaultOnly
            );
        }).collect(Collectors.toList());
    }

    // ✅ 스타일 요약만 따로 반환 (별도 API 용)
    public List<StyleSummaryDto> getStyleSummariesOnly(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        return scraps.stream()
                .flatMap(scrap -> scrap.getNews().getStyleSummaries().stream()
                        .map(s -> new StyleSummaryDto(scrap.getNews().getId(), s.getStyle(), s.getSummaryText())))
                .collect(Collectors.toList());
    }

    public List<NewsEntity> getScrappedNews(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return scrapRepository.findByUser(user).stream()
                .map(ScrapEntity::getNews)
                .toList();
    }
}
