package spl.demo.service;

import org.springframework.stereotype.Service;
import spl.demo.dto.NewsDto;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.NewsEntity;
import spl.demo.repository.NewsRepository;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // ✅ 뉴스 저장 (중복 방지)
    public void saveNewsIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());
            newsEntity.setContent(dto.getContent());
            newsEntity.setCategory(dto.getCategory());

            // createdAt은 NewsEntity에서 @PrePersist로 자동 설정됨
            newsRepository.save(newsEntity);
        }
    }

    // ✅ 전체 뉴스 조회
    public List<NewsEntity> getAllNews() {
        return newsRepository.findAll();
    }

    // ✅ 카테고리별 뉴스 조회
    public List<NewsEntity> getNewsByCategory(InterestCategoryEntity category) {
        return newsRepository.findByCategory(category);
    }
}
