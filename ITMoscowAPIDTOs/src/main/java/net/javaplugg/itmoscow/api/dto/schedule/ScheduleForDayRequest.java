package net.javaplugg.itmoscow.api.dto.schedule;

import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;

/**
 * <h1>Запрос на получение расписания на указанный день</h1>
 * <pre><code>
 * {
 *     "building": {
 *         "name": "Миллионщикова",
 *         "key": "ttm"
 *     },
 *     "group": {
 *         "name": "1ВР-1-25"
 *     },
 *     "weekday": 0,
 *     "replacements": true
 * }
 * </code></pre>
 * @param building строение
 * @param group группа
 * @param weekday индекс дня недели
 * @param replacements применить ли замены
 */
public record ScheduleForDayRequest(Building building, Group group, int weekday, boolean replacements) {
}
