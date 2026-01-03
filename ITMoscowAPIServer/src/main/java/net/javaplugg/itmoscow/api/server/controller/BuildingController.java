package net.javaplugg.itmoscow.api.server.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.building.ListBuildingsResponse;
import net.javaplugg.itmoscow.api.server.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/itmoscow/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/list")
    public CompletableFuture<ResponseEntity<ListBuildingsResponse>> list() {
        return buildingService
                .getAllBuildings()
                .thenApply(ListBuildingsResponse::new)
                .thenApply(ResponseEntity::ok);
    }
}
