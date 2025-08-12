package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardDto {
    private String newsId;
    private String title;
    private String imageUrl;
}
