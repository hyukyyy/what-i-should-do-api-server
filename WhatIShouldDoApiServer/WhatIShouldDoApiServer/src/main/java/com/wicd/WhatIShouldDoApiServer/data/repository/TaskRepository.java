package com.wicd.WhatIShouldDoApiServer.data.repository;

import com.wicd.WhatIShouldDoApiServer.data.dto.TaskDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
