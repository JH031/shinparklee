package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_category_click", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category"})
})
@Getter
@Setter
public class UserCategoryClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SignupEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCategoryEntity category;

    @Column(nullable = false)
    private Long clickCount = 0L;

    public void increment() {
        this.clickCount++;
    }
}
