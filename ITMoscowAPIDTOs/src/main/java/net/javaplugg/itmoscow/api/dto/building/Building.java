package net.javaplugg.itmoscow.api.dto.building;

/**
 * <h1>Корпус колледжа</h1>
 * <pre><code>
 * {
 *     "name": "Миллионщикова",
 *     "key": "ttm"
 * }
 * </code></pre>
 * @param name название корпуса
 * @param key ключ корпуса
 */
public record Building(String name, String key) {
}
