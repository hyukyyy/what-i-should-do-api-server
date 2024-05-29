package com.wicd.WhatIShouldDoApiServer.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @Size(min = 3, max = 50, message = "invalid username length")
    String username;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 3, max = 100, message = "invalid password length")
    String password;
}