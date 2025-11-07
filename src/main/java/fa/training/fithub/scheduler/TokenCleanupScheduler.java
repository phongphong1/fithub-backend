package fa.training.fithub.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
