package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SummaryEntity;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    boolean existsByNews(NewsEntity news);
}
