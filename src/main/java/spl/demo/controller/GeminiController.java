package spl.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spl.demo.service.GeminiService;

@RestController
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @GetMapping("/gemini")
    public ResponseEntity<String> getAnswer(@RequestParam("prompt") String prompt) {
        try {
            String result = geminiService.askGemini(prompt);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

