package com.wicd.WhatIShouldDoApiServer.data.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * DTO for {@link com.wicd.WhatIShouldDoApiServer.data.entity.Task}
 */
@Value
@Builder
public class TaskDto implements Serializable {
    @Length(message = "invalid title length", max = 30)
    String title;
    @Length(message = "invalid content length", max = 2000)
    String content;
    @Min(message = "progress cannot be less than 0", value = 0)
    @Max(message = "progress cannot be more than 0", value = 100)
    Integer progress;
    Long userId;
    @NotNull
    Long teamId;
}