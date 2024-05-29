package com.wicd.WhatIShouldDoApiServer.api.auth.service;

import com.wicd.WhatIShouldDoApiServer.api.auth.controller.AuthController;
import com.wicd.WhatIShouldDoApiServer.api.auth.model.SIGNUP_ERROR_MESSAGE;
import com.wicd.WhatIShouldDoApiServer.api.auth.model.TOKEN_TYPE;
import com.wicd.WhatIShouldDoApiServer.config.auth.JwtFilter;
import com.wicd.WhatIShouldDoApiServer.config.auth.TokenProvider;
import com.wicd.WhatIShouldDoApiServer.data.dto.LoginDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.TokenDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.UserDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.Authority;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import com.wicd.WhatIShouldDoApiServer.data.repository.UserRepository;
import com.wicd.WhatIShouldDoApiServer.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final EntityManager entityManager;

    @Override
    @Transactional
    // 로그인시에 DB에서 유저정보와 권한정보를 가져와서 해당 정보를 기반으로 userdetails.User 객체를 생성해 리턴
    public UserDetails loadUserByUsername(final String username) {

        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> not exist"));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> is not activated");
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }

    @Transactional
    public User signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, SIGNUP_ERROR_MESSAGE.USERNAME_ALREADY_EXIST.getMsg());
        }

        // 가입되어 있지 않은 회원이면,
        // 권한 정보 만들고
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 유저 정보를 만들어서 save
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .refreshToken(null)
                .build();

        return userRepository.save(user);
    }

    public TokenDto login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        String accessToken = tokenProvider.createToken(authentication, TOKEN_TYPE.ACCESS);
        String refreshToken = tokenProvider.createToken(authentication, TOKEN_TYPE.REFRESH);

        User user = userRepository.findByUsername(loginDto.getUsername()).get();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // tokenDto를 이용해 response body에도 넣어서 리턴
        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto refreshToken(HttpServletRequest request) {
        try {
            Optional<String> username = SecurityUtil.getCurrentUsername();
            if (username.isEmpty()) return null;

            Optional<User> user = userRepository.findByUsername(username.get());
            if (user.isEmpty()) return null;

            String token = tokenProvider.resolveToken(request);
            if (!token.equals(user.get().getRefreshToken())) return null;
            logger.debug("token : " + token);

            Authentication authentication = tokenProvider.getAuthentication(token);
            // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
            String accessToken = tokenProvider.createToken(authentication, TOKEN_TYPE.ACCESS);
            String refreshToken = tokenProvider.createToken(authentication, TOKEN_TYPE.REFRESH);

            user.get().setRefreshToken(refreshToken);
            userRepository.save(user.get());

            // tokenDto를 이용해 response body에도 넣어서 리턴
            return new TokenDto(accessToken, refreshToken);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    // 현재 securityContext에 저장된 username의 정보만 가져오는 메소드
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}
