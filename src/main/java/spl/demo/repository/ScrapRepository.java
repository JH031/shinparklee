package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.ScrapEntity;
import spl.demo.entity.SignupEntity;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<ScrapEntity, Long> {
    List<ScrapEntity> findByUser(SignupEntity user);
    boolean existsByUserAndNews(SignupEntity user, NewsEntity news);
    void deleteByUserAndNews(SignupEntity user, NewsEntity news);
    Optional<ScrapEntity> findByUserAndNews(SignupEntity user, NewsEntity news);

}
