package net.javaplugg.itmoscow.api.dto.schedule;

import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;

/**
 * <h1>Запрос на получение сегодняшних замен</h1>
 * <pre><code>
 * {
 *     "building": {
 *         "name": "Миллионщикова",
 *         "key": "ttm"
 *     },
 *     "group": {
 *         "name": "1ВР-1-25"
 *     }
 * }
 * </code></pre>
 * @param building строение
 * @param group группа
 */
public record ReplacementsForTodayRequest(Building building, Group group) {
}
