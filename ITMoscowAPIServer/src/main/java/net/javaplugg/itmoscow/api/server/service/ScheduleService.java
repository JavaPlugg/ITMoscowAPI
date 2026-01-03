package net.javaplugg.itmoscow.api.server.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;
import net.javaplugg.itmoscow.api.dto.schedule.Replacement;
import net.javaplugg.itmoscow.api.dto.schedule.Schedule;

public interface ScheduleService {

    /**
     * Получает расписание для указанного дня
     * @param building строение
     * @param group группа
     * @param weekday индекс дня недели
     * @param replacements применить ли замены
     * @return {@link CompletableFuture} на расписание
     */
    CompletableFuture<Schedule> getScheduleForDay(Building building, Group group, int weekday, boolean replacements);

    /**
     * Получает замены на сегодня
     * @param building строение
     * @param group группа
     * @return {@link CompletableFuture} на список замен
     */
    CompletableFuture<List<Replacement>> getReplacementsForToday(Building building, Group group);
}
