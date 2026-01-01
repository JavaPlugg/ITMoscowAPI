package net.javaplugg.itmoscow.api.server.properties;

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

    @Positive
    private final int otpLifetimeMinutes;

    @Positive
    private final int maxRequestsPerMinute;
}
