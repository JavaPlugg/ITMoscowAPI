package net.javaplugg.itmoscow.api.dto.schedule;

import java.util.List;

/**
 * <h1>Ответ на запрос на получение сегодняшних замен</h1>
 * <pre><code>
 * {
 *     "replacements": [
 *         {
 *             "number": 1,
 *             "subject": "ОУП. 03 Математика",
 *             "teacher": "Данилов Е.И.",
 *             "room": "Академика Миллионщикова. каб: 204"
 *         }
 *     ]
 * }
 * </code></pre>
 * @param replacements замены
 */
public record ReplacementsForTodayResponse(List<Replacement> replacements) {
}
