package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SummaryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    boolean existsByNews(NewsEntity news);

}
