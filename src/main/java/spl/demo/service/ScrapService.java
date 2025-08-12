package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spl.demo.dto.CategoryScrapDto;
import spl.demo.dto.MyPageDto;
import spl.demo.dto.ScrapSummaryDto;
import spl.demo.dto.StyleSummaryDto;
import spl.demo.entity.*;
import spl.demo.repository.*;

import java.util.*;
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

    // ✅ 기본 요약만 포함된 전체 스크랩 목록
    public List<ScrapSummaryDto> getScrappedNewsWithSummaries(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        return scraps.stream().map(scrap -> {
            NewsEntity news = scrap.getNews();
            String defaultSummary = summaryRepository.findByNews(news)
                    .map(SummaryEntity::getSummaryText)
                    .orElse("요약문 없음");

            List<StyleSummaryDto> defaultOnly = List.of(
                    new StyleSummaryDto(news.getId(), SummaryStyle.DEFAULT, defaultSummary)
            );

            return new ScrapSummaryDto(
                    news.getId(),
                    news.getTitle(),
                    news.getUrl(),
                    news.getImageUrl(),
                    defaultOnly
            );
        }).collect(Collectors.toList());
    }

    // ✅ 스타일 요약만 따로 반환
    public List<StyleSummaryDto> getStyleSummariesOnly(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        return scraps.stream()
                .flatMap(scrap -> scrap.getNews().getStyleSummaries().stream()
                        .map(s -> new StyleSummaryDto(scrap.getNews().getId(), s.getStyle(), s.getSummaryText())))
                .collect(Collectors.toList());
    }

    // ✅ 카테고리별 스크랩 목록 반환
    public List<CategoryScrapDto> getScrapsGroupedByCategory(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        // 기본 요약만 포함된 ScrapSummaryDto로 변환
        List<ScrapSummaryDto> scrapDtos = scraps.stream().map(scrap -> {
            NewsEntity news = scrap.getNews();
            String defaultSummary = summaryRepository.findByNews(news)
                    .map(SummaryEntity::getSummaryText)
                    .orElse("요약문 없음");

            StyleSummaryDto summaryDto = new StyleSummaryDto(news.getId(), SummaryStyle.DEFAULT, defaultSummary);

            return new ScrapSummaryDto(
                    news.getId(),
                    news.getTitle(),
                    news.getUrl(),
                    news.getImageUrl(),
                    List.of(summaryDto)
            );
        }).toList();

        // 카테고리별 그룹핑
        Map<InterestCategoryEntity, List<ScrapSummaryDto>> grouped = new HashMap<>();
        for (ScrapEntity scrap : scraps) {
            InterestCategoryEntity category = scrap.getNews().getCategory();
            ScrapSummaryDto dto = scrapDtos.stream()
                    .filter(d -> d.getId().equals(scrap.getNews().getId()))
                    .findFirst()
                    .orElse(null);

            if (dto != null) {
                grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(dto);
            }
        }

        return grouped.entrySet().stream()
                .map(entry -> new CategoryScrapDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<NewsEntity> getScrappedNews(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return scrapRepository.findByUser(user).stream()
                .map(ScrapEntity::getNews)
                .toList();
    }
    public MyPageDto getMyPage(Long userId) {
        SignupEntity user = signupRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<ScrapEntity> scraps = scrapRepository.findByUser(user);

        List<ScrapSummaryDto> scrapSummaries = scraps.stream().map(scrap -> {
            NewsEntity news = scrap.getNews();
            String defaultSummary = summaryRepository.findByNews(news)
                    .map(SummaryEntity::getSummaryText)
                    .orElse("요약문 없음");

            List<StyleSummaryDto> summaries = List.of(
                    new StyleSummaryDto(news.getId(), SummaryStyle.DEFAULT, defaultSummary)
            );

            return new ScrapSummaryDto(
                    news.getId(),
                    news.getTitle(),
                    news.getUrl(),
                    news.getImageUrl(),
                    summaries
            );
        }).toList();

        return new MyPageDto(
                user.getUserId(),
                scraps.size(),
                user.getInterestCategories(),
                scrapSummaries
        );
    }

}
