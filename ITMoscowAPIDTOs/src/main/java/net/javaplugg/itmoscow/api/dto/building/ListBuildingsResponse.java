package net.javaplugg.itmoscow.api.dto.building;

import java.util.List;

/**
 * <h1>Ответ на запрос на получение списка корпусов колледжа</h1>
 * <pre><code>
 * {
 *     "buildings": [
 *         {
 *             "name": "Миллионщикова",
 *             "key": "ttm"
 *         },
 *         {
 *             "name": "Коломенская",
 *             "key": "ttk"
 *         }
 *     ]
 * }
 * </code></pre>
 * @param buildings корпуса
 */
public record ListBuildingsResponse(List<Building> buildings) {
}
