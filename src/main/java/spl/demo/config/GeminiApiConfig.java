package spl.demo.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "gemini.api")
public class GeminiApiConfig {
    private String url;
    private String key;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
