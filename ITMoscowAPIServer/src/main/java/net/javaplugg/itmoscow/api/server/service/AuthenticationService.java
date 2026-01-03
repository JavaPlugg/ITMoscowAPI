package net.javaplugg.itmoscow.api.server.service;

import java.util.Optional;

public interface AuthenticationService {

    /**
     * Генерирует и регистрирует одноразовый пароль (OTP)
     * @param mail Почта, которая будет ассоциирована со сгенерированным OTP
     * @return OTP
     */
    String generateOTP(String mail);

    /**
     * Генерирует и сохраняет токен
     * @param otp Одноразовый пароль, необходимый для получения токена
     * @return токен
     */
    String generateToken(String otp);

    /**
     * Извлекает токен (при его наличии) из хедера авторизации
     * @param authHeader значение хедера авторизации
     * @return {@link java.util.Optional} с токеном (при его наличии)
     */
    Optional<String> extractToken(String authHeader);

    /**
     * Проверяет, что в текущий момент указанный токен подходит для запросов, как метод авторизации
     * @param token токен
     * @return <code>true</code>, если токен существует и активен, иначе <code>false</code>
     */
    boolean validateToken(String token);

    /**
     * Отзывает токен
     * @param token токен
     */
    void revokeToken(String token);
}
