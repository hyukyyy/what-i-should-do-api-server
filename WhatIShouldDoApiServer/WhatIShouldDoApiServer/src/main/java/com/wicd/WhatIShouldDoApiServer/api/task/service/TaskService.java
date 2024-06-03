package com.wicd.WhatIShouldDoApiServer.api.task.service;

import com.wicd.WhatIShouldDoApiServer.data.dto.TaskDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.Task;
import com.wicd.WhatIShouldDoApiServer.data.entity.Team;
import com.wicd.WhatIShouldDoApiServer.data.repository.TeamRepository;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import com.wicd.WhatIShouldDoApiServer.data.repository.TaskRepository;
import com.wicd.WhatIShouldDoApiServer.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TaskDto createTask(TaskDto taskDto) {
        Team team = teamRepository.findByTeamId(taskDto.getTeamId()).orElse(null);
        if (null == team) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "invalid teamId");
        }

        Task task = Task
                .builder()
                .title(taskDto.getTitle())
                .content(taskDto.getContent())
                .progress(taskDto.getProgress())
                .build();
        task.setTeam(team);

        if (null != taskDto.getUserId()) {
            userRepository.findByUserId(taskDto.getUserId()).ifPresent(task::setUser);
        }

        taskRepository.save(task);

        return taskDto;
    }
}
