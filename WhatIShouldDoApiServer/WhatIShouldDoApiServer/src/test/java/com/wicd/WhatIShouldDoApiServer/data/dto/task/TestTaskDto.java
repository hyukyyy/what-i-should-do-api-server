package com.wicd.WhatIShouldDoApiServer.data.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TestTaskDto {
    private String title;
    private String content;
    private Integer progress;
    private Long userId;
    private Long teamId;

    public TestTaskDto(String title, String content, Long teamId) {
        this.title = title;
        this.content = content;
        this.teamId = teamId;
    }
}
