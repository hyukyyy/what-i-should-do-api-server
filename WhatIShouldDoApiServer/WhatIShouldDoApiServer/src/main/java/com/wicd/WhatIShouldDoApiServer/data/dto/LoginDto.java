package com.wicd.WhatIShouldDoApiServer.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * DTO for {@link com.wicd.WhatIShouldDoApiServer.data.entity.User}
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@Value
public class LoginDto implements Serializable {

    @NotBlank(message = "username cannot be blank")
    String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "password cannot be blank")
    String password;
}