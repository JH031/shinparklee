package spl.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spl.demo.config.GeminiApiConfig;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiApiConfig config;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String summarizeTo4Lines(String content) throws Exception {
        String prompt = "다음 뉴스 내용을 4줄로 요약해줘:\n" + content;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String fullUrl = config.getUrl() + "?key=" + config.getKey();
        ResponseEntity<String> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 응답에서 요약 텍스트 추출
        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
    }
}
