package spl.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class SignupDto {
    private String username;
    private String userId;
    private String password;
    private String confirmPassword;
    private String email;
    private List<InterestCategoryEntity> interestCategories;
    private SummaryStyle style;

}