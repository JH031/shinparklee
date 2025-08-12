package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spl.demo.entity.InterestCategoryEntity;
import spl.demo.entity.SummaryStyle;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MyPageDto {
    private String userId;
    private int scrapCount;
    private List<InterestCategoryEntity> interestCategories;
    private List<ScrapSummaryDto> scraps;
}
