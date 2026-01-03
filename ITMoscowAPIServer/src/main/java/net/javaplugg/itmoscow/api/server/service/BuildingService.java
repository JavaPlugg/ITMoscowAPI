package net.javaplugg.itmoscow.api.server.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.javaplugg.itmoscow.api.dto.building.Building;

public interface BuildingService {

    /**
     * Получает список всех корпусов колледжа
     * @return {@link java.util.concurrent.CompletableFuture} на список всех корпусов колледжа
     */
    CompletableFuture<List<Building>> getAllBuildings();
}
