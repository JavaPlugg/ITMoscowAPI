package net.javaplugg.itmoscow.api.dto.schedule;

/**
 * <h1>Урок</h1>
 * <pre><code>
 * {
 *     "number": 1,
 *     "time": "08:30 - 09:15",
 *     "subject": "ОУП. 03 Математика",
 *     "teacher": "Данилов Е.И.",
 *     "room": "Академика Миллионщикова. каб: 204"
 * }
 * </code></pre>
 * @param number номер урока
 * @param time время начала и конца
 * @param subject предмет
 * @param teacher преподаватель
 * @param room помещение
 */
public record Lesson(
        int number,
        String time,
        String subject,
        String teacher,
        String room
) {
}
