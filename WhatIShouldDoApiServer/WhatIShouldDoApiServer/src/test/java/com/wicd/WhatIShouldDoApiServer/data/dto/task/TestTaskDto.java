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

    public TestTaskDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public TestTaskDto(String title2, String content2, Integer progress) {
        this.title = title;
        this.content = content;
        this.progress = progress;
    }
}
