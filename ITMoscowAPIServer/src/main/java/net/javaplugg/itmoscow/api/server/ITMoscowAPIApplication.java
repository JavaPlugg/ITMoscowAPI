package net.javaplugg.itmoscow.api.server;

import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ITMoscowAPIServerProperties.class)
public class ITMoscowAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(ITMoscowAPIApplication.class, args);
    }
}