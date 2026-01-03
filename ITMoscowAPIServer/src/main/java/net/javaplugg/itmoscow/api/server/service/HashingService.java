package net.javaplugg.itmoscow.api.server.service;

public interface HashingService {

    /**
     * Хэширует пароль
     * @param password пароль
     * @return хэш
     */
    String hash(String password);

    /**
     * Проверяет пароль на соответствие хэшу
     * @param password пароль
     * @param hash хэш
     * @return результат проверки
     */
    boolean verify(String password, String hash);
}
