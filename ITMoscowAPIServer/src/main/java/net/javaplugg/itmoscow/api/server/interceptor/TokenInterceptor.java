package net.javaplugg.itmoscow.api.server.interceptor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import net.javaplugg.itmoscow.api.server.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>Блокирует запросы к API без токена</p>
 * <p>Ограничивает количество запросов по токену в минуту</p>
 */
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final ITMoscowAPIServerProperties properties;
    private final AuthenticationService authenticationService;
    private final LoadingCache<String, AtomicInteger> requestAmountCache = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .build(key -> new AtomicInteger(0));

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String authHeader = request.getHeader("Authorization");
        String token = authenticationService.extractToken(authHeader).orElse(null);
        if (token == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        AtomicInteger amount = requestAmountCache.get(token);
        if (amount == null) {
            throw new IllegalStateException();
        }
        if (amount.incrementAndGet() > properties.getMaxRequestsPerMinute()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        boolean authorized = authenticationService.validateToken(token);
        if (!authorized) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
