package fa.training.fithub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Security Configuration cho Development Environment
         * - CORS enabled cho localhost
         * - CSRF disabled
         * - All auth endpoints public
         * - Stateless session management
         */
        @Bean
        @Profile("dev")
        public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS configuration
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // CSRF disabled cho DEV environment
                                .csrf(csrf -> csrf.disable())

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints - không cần authentication
                                                // Note: Spring Security matches after context-path is removed
                                                .requestMatchers(
                                                                "/auth/**", // Authentication endpoints (without /api
                                                                            // context)
                                                                "/public/**", // Public API
                                                                "/error", // Error endpoint
                                                                "/actuator/health" // Health check
                                                ).permitAll()

                                                // All other requests need authentication
                                                .anyRequest().authenticated())

                                // Session management - stateless (JWT)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        /**
         * Security Configuration cho Production Environment
         * - CORS restricted
         * - CSRF enabled
         * - Stricter security settings
         */
        @Bean
        @Profile("prod")
        public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS configuration
                                .cors(cors -> cors.configurationSource(prodCorsConfigurationSource()))

                                // CSRF enabled for PROD
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**")) // Without /api context

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                // Note: Spring Security matches after context-path is removed
                                                .requestMatchers(
                                                                "/auth/register", // Without /api context
                                                                "/auth/login",
                                                                "/auth/verify-email",
                                                                "/auth/resend-verification-email",
                                                                "/auth/forgot-password",
                                                                "/error")
                                                .permitAll()

                                                // All other requests need authentication
                                                .anyRequest().authenticated())

                                // Session management - stateless (JWT)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        /**
         * CORS Configuration cho Development
         * Cho phép tất cả origins, methods, headers
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow multiple origins for development
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://localhost:4200",
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:5173"));

                // Allow all HTTP methods
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                // Allow all headers
                configuration.setAllowedHeaders(Arrays.asList("*"));

                // Allow credentials (cookies, authorization headers)
                configuration.setAllowCredentials(true);

                // Expose headers
                configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

                // Max age for preflight requests
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * CORS Configuration cho Production
         * Chỉ cho phép specific origins
         */
        public CorsConfigurationSource prodCorsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Only allow production domain
                configuration.setAllowedOrigins(List.of(
                                "https://fithub.com",
                                "https://www.fithub.com"));

                // Allow specific methods
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

                // Allow specific headers
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Requested-With"));

                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}
