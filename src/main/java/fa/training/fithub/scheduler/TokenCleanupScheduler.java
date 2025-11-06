package fa.training.fithub.scheduler;

import fa.training.fithub.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

//    private final TokenRepository tokenRepository;
//
//    // Dọn token hết hạn mỗi ngày lúc 3h sáng
//    @Scheduled(cron = "0 0 3 * * *")
//    public void cleanExpiredTokens() {
//        int deleted = tokenRepository.deleteAllExpiredTokens(Instant.now());
//        if (deleted > 0) {
//            log.info("Cleaned up {} expired/inactive tokens", deleted);
//        }
//    }
}
