package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScrapSummaryDto {
    private Long id;
    private String title;
    private String url;
    private String imageUrl;
    private List<StyleSummaryDto> summaries;
}
