package net.javaplugg.itmoscow.api.server.configuration;

import lombok.RequiredArgsConstructor;
import net.javaplugg.itmoscow.api.server.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class TokenConfiguration implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/itmoscow/api/**");
    }
}
