package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private String username;
    private String userId;
    private String email;
    private List<InterestCategoryEntity> interestCategories;
    private SummaryStyle style;
}
