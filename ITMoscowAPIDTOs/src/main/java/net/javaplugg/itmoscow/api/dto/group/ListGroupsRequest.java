package net.javaplugg.itmoscow.api.dto.group;

import net.javaplugg.itmoscow.api.dto.building.Building;

/**
 * <h1>Запрос на получение списка групп</h1>
 * <pre><code>
 * {
 *     "building": {
 *         "name": "Миллионщикова",
 *         "key": "ttm"
 *     }
 * }
 * </code></pre>
 * @param building корпус
 */
public record ListGroupsRequest(Building building) {
}
