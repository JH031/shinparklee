package spl.demo.dto;

import lombok.Data;
import spl.demo.entity.InterestCategoryEntity;

@Data
public class NewsDto {
    private String newsId;
    private String title;
    private String url;
    private String content;
    private String imageUrl;
    private InterestCategoryEntity category;
    private boolean isScrapped;
}
