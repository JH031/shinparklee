package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "news")
public class NewsEntity {

    // Getter/Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String newsId;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false)
    private String url;

    @Setter
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCategoryEntity category;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 기본 생성자
    public NewsEntity() {}

}
