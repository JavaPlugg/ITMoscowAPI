package net.javaplugg.itmoscow.api.server.filter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class IpRateLimiterFilter extends OncePerRequestFilter {

    private final LoadingCache<String, AtomicInteger> requestAmountCache = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .build(key -> new AtomicInteger(0));

    private final ITMoscowAPIServerProperties properties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        AtomicInteger amount = requestAmountCache.get(ip);
        if (amount == null) {
            throw new IllegalStateException();
        }
        if (amount.incrementAndGet() > properties.getMaxRequestsPerMinute()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
