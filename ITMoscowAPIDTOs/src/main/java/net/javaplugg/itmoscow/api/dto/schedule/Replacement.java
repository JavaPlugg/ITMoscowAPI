package net.javaplugg.itmoscow.api.dto.schedule;

/**
 * <h1>Замена урока</h1>
 * <pre><code>
 * {
 *     "number": 1,
 *     "subject": "ОУП. 03 Математика",
 *     "teacher": "Данилов Е.И.",
 *     "room": "Академика Миллионщикова. каб: 204"
 * }
 * </code></pre>
 * @param number номер урока
 * @param subject новый предмет
 * @param teacher новый преподаватель
 * @param room новое помещение
 */
public record Replacement(
        int number,
        String subject,
        String teacher,
        String room
) {
}
