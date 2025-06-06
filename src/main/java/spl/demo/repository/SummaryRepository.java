package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.List;
import java.util.Optional;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    boolean existsByNews(NewsEntity news);
    Optional<SummaryEntity> findByNews(NewsEntity news);
    @Query("SELECT s FROM SummaryEntity s WHERE s.news.hotTopic = true")
    List<SummaryEntity> findByNewsIn(List<NewsEntity> newsList);

    Optional<SummaryEntity> findByNews_NewsId(String newsId);

}
