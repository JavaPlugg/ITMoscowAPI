package net.javaplugg.itmoscow.api.server.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.ApiResponse;
import net.javaplugg.itmoscow.api.dto.group.ListGroupsRequest;
import net.javaplugg.itmoscow.api.dto.group.ListGroupsResponse;
import net.javaplugg.itmoscow.api.server.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/itmoscow/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/list")
    public CompletableFuture<ResponseEntity<ApiResponse>> list(@RequestBody ListGroupsRequest request) {
        return groupService
                .getGroupsByBuilding(request.building())
                .thenApply(ListGroupsResponse::new)
                .thenApply(ResponseEntity::ok);
    }
}
