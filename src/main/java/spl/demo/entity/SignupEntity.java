package spl.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor

public class SignupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //db에 저장되는 순번


    @Column(unique = true)
    private String username; //실제이름

    @Column(unique = true, nullable = false)
    private String userId; //가입자 아이디

    private String password;

    private String email;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<InterestCategoryEntity> interestCategories;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SummaryStyle style;


}
