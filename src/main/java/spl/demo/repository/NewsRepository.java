package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.NewsEntity;

import java.util.List;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {

    // ✅ 중복 저장 방지용
    boolean existsByNewsId(String newsId);

    // ✅ 카테고리별 뉴스 목록 조회
    List<NewsEntity> findByCategory(InterestCategoryEntity category);

    List<NewsEntity> findByHotTopicTrue();

    List<NewsEntity> findByTitleContainingIgnoreCase(String keyword);
}
