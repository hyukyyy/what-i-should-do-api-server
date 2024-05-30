package com.wicd.WhatIShouldDoApiServer.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class TestLoginDto implements Serializable {
    String username;
    String password;
}
