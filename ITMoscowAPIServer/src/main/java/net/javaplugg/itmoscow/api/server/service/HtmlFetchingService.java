package net.javaplugg.itmoscow.api.server.service;

import java.util.concurrent.CompletableFuture;

public interface HtmlFetchingService {

    /**
     * Получает html страницы по указанному адресу
     * @param url адрес
     * @return {@link java.util.concurrent.CompletableFuture} на полученный html
     */
    CompletableFuture<String> fetchHtml(String url);
}
