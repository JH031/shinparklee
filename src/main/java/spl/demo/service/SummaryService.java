package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spl.demo.entity.*;
import spl.demo.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SignupRepository signupRepository;
    private final NewsRepository newsRepository;
    private final SummaryRepository summaryRepository;
    private final StyleSummaryRepository styleSummaryRepository;
    private final GeminiService geminiService;

    /**
     * 🔥 핫토픽 뉴스 전체 요약 (기본 + 사용자의 말투로)
     */
    @Transactional
    public void summarizeHotNewsForUser(Long signupUserId) {
        SignupEntity user = signupRepository.findById(signupUserId)
                .orElseThrow(() -> new RuntimeException("❌ 사용자 없음: id=" + signupUserId));

        SummaryStyle style = user.getStyle();

        // ✅ 핫토픽 뉴스만 가져오기
        List<NewsEntity> hotNewsList = newsRepository.findByHotTopicTrue();

        for (NewsEntity news : hotNewsList) {
            Long newsId = news.getId();

            try {
                // ✅ 기본 요약
                summaryRepository.findByNews_Id(newsId).ifPresentOrElse(
                        existing -> {
                            String summary = geminiService.summarizeTo4Lines(news.getContent());
                            existing.updateSummaryText(summary);
                            summaryRepository.save(existing);
                        },
                        () -> {
                            String summary = geminiService.summarizeTo4Lines(news.getContent());
                            summaryRepository.save(new SummaryEntity(news, summary));
                        }
                );

                // ✅ 말투 요약
                styleSummaryRepository.findByNewsIdAndStyle(newsId, style).ifPresentOrElse(
                        existing -> {
                            String styled = geminiService.generateStyledSummary(news.getContent(), style);
                            existing.updateSummaryText(styled);
                            styleSummaryRepository.save(existing);
                        },
                        () -> {
                            String styled = geminiService.generateStyledSummary(news.getContent(), style);
                            styleSummaryRepository.save(new StyleSummaryEntity(news, styled, style));
                        }
                );
            } catch (Exception e) {
                System.err.println("❌ 요약 실패 - newsId=" + newsId);
            }
        }
    }
}
