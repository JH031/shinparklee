package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "summary")
public class SummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT") // ✅ 수정
    private String summaryText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SummaryEntity(NewsEntity news, String summaryText) {
        this.news = news;
        this.summaryText = summaryText;
        this.createdAt = LocalDateTime.now();
    }
}
