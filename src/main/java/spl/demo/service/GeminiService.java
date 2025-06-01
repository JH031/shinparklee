package spl.demo.service;

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

    public String askGemini(String prompt) throws Exception {
        // 요청 바디 구성
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

        return response.getBody();
    }
}
