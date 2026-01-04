package net.javaplugg.itmoscow.api.dto.schedule;

import net.javaplugg.itmoscow.api.dto.ApiResponse;

/**
 * <h1>Ответ на запрос на получение расписания на указанный день</h1>
 * <pre><code>
 * {
 *     "schedule": {
 *         "weekday": "Понедельник",
 *         "lessons": [
 *             {
 *                 "number": 1,
 *                 "time": "08:30 - 09:15",
 *                 "subject": "ОУП. 03 Математика",
 *                 "teacher": "Данилов Е.И.",
 *                 "room": "Академика Миллионщикова. каб: 204"
 *             }
 *         ]
 *     }
 * }
 * </code></pre>
 * @param schedule расписание
 */
public record ScheduleForDayResponse(Schedule schedule) implements ApiResponse {
}
