package fa.training.fithub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration cho SSE (Server-Sent Events)
 */
@Configuration
@EnableAsync
@EnableScheduling
public class SseConfiguration implements WebMvcConfigurer {

    /**
     * Cấu hình async request timeout
     * Timeout này áp dụng cho tất cả async requests bao gồm SSE
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // Timeout 30 phút (30 * 60 * 1000 ms)
        // Sau thời gian này, connection sẽ tự động đóng và client cần reconnect
        configurer.setDefaultTimeout(30 * 60 * 1000L);
    }
}
