package net.javaplugg.itmoscow.api.dto;

import java.util.Map;

/**
 * <h1>Ошибка</h1>
 * <pre><code>
 * {
 *     "message": "Токен для указанной почты уже существует"
 *     "details": { ... }
 * }
 * </code></pre>
 * @param message сообщение
 * @param details детали
 */
public record ErrorResponse(String message, Map<String, Object> details) {

    public ErrorResponse(String message) {
        this(message, null);
    }
}