package spl.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spl.demo.entity.SummaryStyle;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.url}")  // 변경됨
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;


    private final RestTemplate restTemplate = new RestTemplate();

    public String generateSummary(String prompt) {
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(contentPart));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String requestUrl = geminiUrl + "?key=" + apiKey;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, request, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) contentMap.get("parts");
            return parts.get(0).get("text");
        } catch (Exception e) {
            e.printStackTrace();
            return "요약 중 오류 발생";
        }
    }

    // 기본 4~5줄 요약
    public String summarizeTo4Lines(String content) {
        String prompt = "다음 뉴스를 4~5문장으로 요약해줘.\n\n" + content;
        return generateSummary(prompt);
    }

    //  말투 기반 요약
    public String generateStyledSummary(String content, SummaryStyle style) {
        String stylePrompt = switch (style) {
            case FUNNY -> "재밌게 요약해줘.";
            case SIMPLE -> "쉽게 요약해줘.";
            case FRIENDLY -> "친구한테 말해듯이 요약해줘.";
            default -> "요약해줘.";
        };
        return generateSummary(stylePrompt + "\n\n" + content);
    }
}
