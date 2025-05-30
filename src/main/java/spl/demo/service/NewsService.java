package spl.demo.service;

import org.springframework.stereotype.Service;
import spl.demo.dto.NewsDto;
import spl.demo.entity.NewsEntity;
import spl.demo.repository.NewsRepository;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void saveNewsIfNotExists(NewsDto dto) {
        if (!newsRepository.existsByNewsId(dto.getNewsId())) {
            NewsEntity newsEntity = new NewsEntity(); // ✅ 타입이 정확히 NewsEntity
            newsEntity.setNewsId(dto.getNewsId());
            newsEntity.setTitle(dto.getTitle());
            newsEntity.setUrl(dto.getUrl());
            newsEntity.setContent(dto.getContent());
            newsEntity.setCategory(dto.getCategory());
            newsRepository.save(newsEntity); // ✅ 정확한 타입이라 오류 없음
        }
    }
}
