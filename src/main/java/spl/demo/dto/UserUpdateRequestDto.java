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
public class UserUpdateRequestDto {
    private List<InterestCategoryEntity> interestCategories;
    private SummaryStyle style;
}
