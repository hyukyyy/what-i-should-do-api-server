package com.wicd.WhatIShouldDoApiServer.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
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
public class UserDto implements Serializable {
    @Email(message = "username pattern invalid")
    @NotBlank(message = "username cannot be blank")
    @Size(min = 3, max = 50, message = "username size error")
    String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 3, max = 50, message = "password size error")
    @NotBlank(message = "password cannot be blank")
    String password;

    @Size(min = 3, max = 50, message = "nickname size error")
    @NotBlank(message = "nickname cannot be blank")
    String nickname;
}