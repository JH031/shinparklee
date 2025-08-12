package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "scraps")
public class ScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SignupEntity user;

    @ManyToOne
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ 사용자 스타일에 맞는 요약 반환
    public String getStyledSummary(SummaryStyle style) {
        return news.getStyleSummaries().stream()
                .filter(s -> s.getStyle() == style)
                .map(StyleSummaryEntity::getSummaryText) // 🔁 getSummary → getSummaryText로 수정
                .findFirst()
                .orElse(null); // 없으면 null 반환 (기본 요약은 따로 처리 가능)
    }
}
