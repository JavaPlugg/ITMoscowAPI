package net.javaplugg.itmoscow.api.server.service;

import java.util.Optional;
import net.javaplugg.itmoscow.api.server.dao.TokenDao;

public interface TokenPersistenceService {

    /**
     * Создает и сохраняет токен
     * @param tokenId айди токена
     * @param email электронная почта
     * @param random случайная часть токена
     * @return сохраненный {@link TokenDao}
     */
    TokenDao createToken(String tokenId, String email, String random);

    /**
     * Получает {@link TokenDao} по текстовому токену
     * @param rawToken текстовый токен
     * @return {@link Optional} с {@link TokenDao}
     */
    Optional<TokenDao> getTokenByRawToken(String rawToken);

    /**
     * Получает токен по электронной почте
     * @param email адрес почты
     * @return {@link Optional} с токеном
     */
    Optional<TokenDao> getTokenByEmail(String email);

    /**
     * Удаляет токен по его айди
     * @param tokenId айди токена
     */
    void deleteTokenById(String tokenId);
}
