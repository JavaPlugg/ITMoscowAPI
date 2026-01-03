package net.javaplugg.itmoscow.api.dto.auth;

/**
 * <h1>Запрос на запрос одноразового пароля</h1>
 * <pre><code>
 * {
 *     "email": "example@gmail.com"
 * }
 * </code></pre>
 * @param email электронная почта
 */
public record RequestOTPRequest(String email) {
}
