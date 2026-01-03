package net.javaplugg.itmoscow.api.server.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HtmlFetchingServiceImpl implements HtmlFetchingService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public CompletableFuture<String> fetchHtml(String url) {
        return httpClient.sendAsync(HttpRequest
                        .newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).thenApply(HttpResponse::body);
    }
}
