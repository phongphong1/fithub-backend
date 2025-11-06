package fa.training.fithub.util;

import fa.training.fithub.entity.SystemConfig;
import fa.training.fithub.entity.User;
import fa.training.fithub.repository.SystemConfigRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final SystemConfigRepository systemConfigRepository;

    private SecretKey secretKey;

    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;

    @Value("${app.jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        // Khởi tạo SecretKey
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Lấy expiration từ database
        Optional<SystemConfig> accessConfig = systemConfigRepository.findByConfigKey("access_token_expiry_minutes");
        Optional<SystemConfig> refreshConfig = systemConfigRepository.findByConfigKey("refresh_token_expiry_days");

        accessTokenExpirationMs = accessConfig
                .map(c -> Long.parseLong(c.getConfigValue()) * 60 * 1000) // phút → ms
                .orElse(60 * 60 * 1000L); // default 1h

        refreshTokenExpirationMs = refreshConfig
                .map(c -> Long.parseLong(c.getConfigValue()) * 24 * 60 * 60 * 1000) // ngày → ms
                .orElse(30 * 24 * 60 * 60 * 1000L); // default 30 ngày
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims decodeToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public long getAccessExpSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    public long getRefreshExpSeconds() {
        return refreshTokenExpirationMs / 1000;
    }
}
