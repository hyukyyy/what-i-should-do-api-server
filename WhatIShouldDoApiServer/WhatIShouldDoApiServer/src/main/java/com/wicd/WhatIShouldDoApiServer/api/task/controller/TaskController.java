package com.wicd.WhatIShouldDoApiServer.api.task.controller;

import com.wicd.WhatIShouldDoApiServer.api.auth.service.AuthService;
import com.wicd.WhatIShouldDoApiServer.api.task.service.TaskService;
import com.wicd.WhatIShouldDoApiServer.data.dto.TaskDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.Task;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("")
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.createTask(taskDto));
    }
}
