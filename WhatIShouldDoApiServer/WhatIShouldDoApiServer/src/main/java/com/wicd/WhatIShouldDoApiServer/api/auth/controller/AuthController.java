package com.wicd.WhatIShouldDoApiServer.api.auth.controller;

import com.wicd.WhatIShouldDoApiServer.api.auth.service.AuthService;
import com.wicd.WhatIShouldDoApiServer.config.auth.TokenProvider;
import com.wicd.WhatIShouldDoApiServer.data.dto.LoginDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.TokenDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.UserDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userDetailsService.signup(userDto));
    }

    @PostMapping("/authenticate")
    public TokenDto authorize(@Valid @RequestBody LoginDto loginDto) {
        return userDetailsService.login(loginDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.refreshToken(request));
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        return ResponseEntity.ok(userDetailsService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/{username}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userDetailsService.getUserWithAuthorities(username).get());
    }

}
