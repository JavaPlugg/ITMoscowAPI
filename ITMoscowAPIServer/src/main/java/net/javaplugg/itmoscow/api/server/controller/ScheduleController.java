package net.javaplugg.itmoscow.api.server.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.ErrorResponse;
import net.javaplugg.itmoscow.api.dto.schedule.ReplacementsForTodayRequest;
import net.javaplugg.itmoscow.api.dto.schedule.ReplacementsForTodayResponse;
import net.javaplugg.itmoscow.api.dto.schedule.ScheduleForDayRequest;
import net.javaplugg.itmoscow.api.dto.schedule.ScheduleForDayResponse;
import net.javaplugg.itmoscow.api.server.exception.CannotApplyReplacementsException;
import net.javaplugg.itmoscow.api.server.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/itmoscow/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    public static final String ERROR_GENERAL = "Возникла внутренняя ошибка сервера";

    private final ScheduleService scheduleService;

    @PostMapping("/day")
    public CompletableFuture<ResponseEntity<Object>> day(@RequestBody ScheduleForDayRequest request) {
        return scheduleService.getScheduleForDay(
                        request.building(),
                        request.group(),
                        request.weekday(),
                        request.replacements()
                ).thenApply(ScheduleForDayResponse::new)
                .thenApply(schedule -> ResponseEntity.ok((Object) schedule))
                .exceptionally(throwable -> throwable.getCause() instanceof CannotApplyReplacementsException
                        ? ResponseEntity.badRequest().body(new ErrorResponse("Нельзя применить замены к расписанию не сегодняшнего дня"))
                        : ResponseEntity.internalServerError().body(new ErrorResponse(ERROR_GENERAL))
                );
    }

    @PostMapping("/replacements")
    public CompletableFuture<ResponseEntity<ReplacementsForTodayResponse>> replacements(@RequestBody ReplacementsForTodayRequest request) {
        return scheduleService.getReplacementsForToday(
                        request.building(),
                        request.group()
                ).thenApply(ReplacementsForTodayResponse::new)
                .thenApply(ResponseEntity::ok);
    }
}
