package spl.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spl.demo.entity.SummaryStyle;

import java.util.EnumMap;

@Getter
@Setter
@AllArgsConstructor
public class ScrapSummaryDto {
    private Long id;
    private String title;
    private String url;
    private EnumMap<SummaryStyle, String> summaries;
}
