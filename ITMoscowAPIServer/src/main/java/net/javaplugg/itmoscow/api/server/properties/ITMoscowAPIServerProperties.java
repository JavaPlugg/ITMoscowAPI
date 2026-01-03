package net.javaplugg.itmoscow.api.server.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = "itmoscow-api-server")
public class ITMoscowAPIServerProperties {

    @NotBlank
    private final String itmoscowUrl;

    @Positive
    private final int otpCacheLifetimeMinutes;

    @Positive
    private final int tokenCacheLifetimeMinutes;

    @Positive
    private final int cacheLifetimeMinutes;

    @Positive
    private final int maxRequestsPerMinute;
}
