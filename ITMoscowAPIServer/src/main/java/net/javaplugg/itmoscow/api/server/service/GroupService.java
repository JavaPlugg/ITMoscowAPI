package net.javaplugg.itmoscow.api.server.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;

public interface GroupService {

    /**
     * Получает список групп в указанном корпусе
     * @param building корпус
     * @return {@link CompletableFuture} на список групп
     */
    CompletableFuture<List<Group>> getGroupsByBuilding(Building building);
}
