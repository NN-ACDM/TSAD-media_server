package tsad.media.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("https://astral-containers.com", "https://www.astral-containers.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
