package net.javaplugg.itmoscow.api.dto.group;

import java.util.List;

/**
 * <h1>Ответ на запрос на получение списка групп</h1>
 * <pre><code>
 * {
 *     "groups": [
 *         {
 *             "name": "1ВР-1-25"
 *         },
 *         {
 *             "name": "1ИП-1-25 (п)"
 *         }
 *     ]
 * }
 * </code></pre>
 * @param groups группы
 */
public record ListGroupsResponse(List<Group> groups) {
}
