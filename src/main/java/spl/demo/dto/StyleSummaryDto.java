package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spl.demo.entity.SummaryStyle;

@Getter
@AllArgsConstructor
public class StyleSummaryDto {
    private Long newsId;               // 어떤 뉴스의 요약인지 알 수 있도록
    private SummaryStyle style;
    private String summaryText;
}
