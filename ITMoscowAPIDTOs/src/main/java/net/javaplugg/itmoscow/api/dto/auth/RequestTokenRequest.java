package net.javaplugg.itmoscow.api.dto.auth;

/**
 * <h1>Запрос на запрос токена</h1>
 * <pre><code>
 * {
 *     "otp": "example"
 * }
 * </code></pre>
 * @param otp
 */
public record RequestTokenRequest(String otp) {
}
