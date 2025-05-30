package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.NewsEntity;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    boolean existsByNewsId(String newsId);
}
