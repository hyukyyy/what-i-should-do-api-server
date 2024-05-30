package com.wicd.WhatIShouldDoApiServer.data.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class TestUserDto implements Serializable {
    String username;
    String password;
    String nickname;
}
