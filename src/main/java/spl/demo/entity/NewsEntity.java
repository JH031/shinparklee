package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "news")
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String newsId; // 네이버 뉴스 고유 ID

    @Setter
    @Column(nullable = false)
    private String title; // 뉴스 제목

    @Setter
    @Column(nullable = false)
    private String url; // 뉴스 링크

    @Setter
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content; // 뉴스 본문

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCategoryEntity category; // 뉴스 카테고리 (enum)

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt; // 등록 시간

    @Column(name = "is_hot_topic")
    private boolean hotTopic;

    // 생성 시 createdAt 자동 설정
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // 기본 생성자
    public NewsEntity() {}
}
