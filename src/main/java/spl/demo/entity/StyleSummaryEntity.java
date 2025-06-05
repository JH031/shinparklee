package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "style_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"news_id", "style"}) // ✅ 중복 방지
)
public class StyleSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @Enumerated(EnumType.STRING)
    @Column(name = "style", nullable = false)
    private SummaryStyle style;

    public StyleSummaryEntity(NewsEntity news, String summaryText, SummaryStyle style) {
        this.news = news;
        this.summaryText = summaryText;
        this.style = style;
    }

    public void updateSummaryText(String newText) {
        this.summaryText = newText;
    }
}
