package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import spl.demo.entity.SummaryStyle;

import java.time.LocalDateTime;
import java.util.EnumMap;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SummaryNewsDto {
    private String newsId;
    private String title;
    private String url;
    private LocalDateTime createdAt;
    private EnumMap<SummaryStyle, String> summaries;
}
