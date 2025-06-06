package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.StyleSummaryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.List;
import java.util.Optional;

public interface StyleSummaryRepository extends JpaRepository<StyleSummaryEntity, Long> {
    Optional<StyleSummaryEntity> findByNews_IdAndStyle(Long newsId, SummaryStyle style);
    List<StyleSummaryEntity> findByNewsIn(List<NewsEntity> newsList);
    List<StyleSummaryEntity> findByStyle(SummaryStyle style);

    List<StyleSummaryEntity> findByNews_NewsId(String newsId);

}
