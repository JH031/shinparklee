package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spl.demo.entity.InterestCategoryEntity;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryScrapDto {
    private InterestCategoryEntity category;
    private List<ScrapSummaryDto> scraps;
}
