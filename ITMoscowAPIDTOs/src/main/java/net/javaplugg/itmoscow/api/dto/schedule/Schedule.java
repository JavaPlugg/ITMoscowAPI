package net.javaplugg.itmoscow.api.dto.schedule;

import java.util.List;

/**
 * <h1>Расписание на день</h1>
 * <pre><code>
 * {
 *     "weekday": "Понедельник",
 *     "lessons": [
 *         {
 *             "number": 1,
 *             "time": "08:30 - 09:15",
 *             "subject": "ОУП. 03 Математика",
 *             "teacher": "Данилов Е.И.",
 *             "room": "Академика Миллионщикова. каб: 204"
 *         }
 *     ]
 * }
 * </code></pre>
 * @param weekday название дня недели
 * @param lessons уроки
 */
public record Schedule(String weekday, List<Lesson> lessons) {
}
