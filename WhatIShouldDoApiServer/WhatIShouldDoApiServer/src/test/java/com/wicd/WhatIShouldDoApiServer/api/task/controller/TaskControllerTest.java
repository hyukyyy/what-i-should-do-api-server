package com.wicd.WhatIShouldDoApiServer.api.task.controller;

import com.wicd.WhatIShouldDoApiServer.api.auth.model.TOKEN_TYPE;
import com.wicd.WhatIShouldDoApiServer.config.auth.TokenProvider;
import com.wicd.WhatIShouldDoApiServer.data.dto.task.TestTaskDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.Authority;
import com.wicd.WhatIShouldDoApiServer.data.entity.Task;
import com.wicd.WhatIShouldDoApiServer.data.entity.Team;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import com.wicd.WhatIShouldDoApiServer.data.repository.TeamRepository;
import com.wicd.WhatIShouldDoApiServer.data.repository.UserRepository;
import com.wicd.WhatIShouldDoApiServer.utils.RandomStringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    int port;

    String username = RandomStringUtil.createRandomLengthEmailPatternString(10, 20);
    String password = RandomStringUtil.createRandomLengthEmailPatternString(10, 20);
    String nickname = RandomStringUtil.createRandomLengthEmailPatternString(10, 20);
    String teamName = RandomStringUtil.createRandomLengthEmailPatternString(10, 20);
    Long userId;
    Long teamId;
    String accessToken;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private TokenProvider tokenProvider;

    @BeforeEach
    public void createDummyUser() {
        userRepository.deleteAll();
        teamRepository.deleteAll();

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = userRepository.save(
                User
                        .builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .nickname(nickname)
                        .authorities(Collections.singleton(authority))
                        .activated(true)
                        .refreshToken(null)
                        .build()
        );

        Team team = teamRepository.save(
                Team
                        .builder()
                        .name(teamName)
                        .build()
        );

        this.userId = user.getUserId();
        this.teamId = team.getTeamId();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        this.accessToken = tokenProvider.createToken(authentication, TOKEN_TYPE.ACCESS);
    }

    @DisplayName("create task")
    @Test
    void createTaskTest() {
        String url = "http://localhost:" + port + "/task";

//        1. 제목 / 내용 입력
        String title = RandomStringUtil.createRandomLengthString(0, 30);
        String content = RandomStringUtil.createRandomLengthString(0, 2000);

        TestTaskDto taskDto = new TestTaskDto(title, content, teamId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.accessToken);
//        headers.set("Content-Type", "application/json");

        // HttpEntity 생성 (헤더를 포함)
        HttpEntity<TestTaskDto> entity = new HttpEntity<>(taskDto, headers);

        ResponseEntity<TestTaskDto> response = restTemplate.postForEntity(url, entity, TestTaskDto.class);

        assert (response.getStatusCode().is2xxSuccessful());

        //        2. 제목 / 내용 / 진행도 입력
        Integer progress = (int) (Math.random() * 100);

        taskDto.setProgress(progress);
        ResponseEntity<TestTaskDto> response2 = restTemplate.postForEntity(url, entity, TestTaskDto.class);

        assert (response2.getStatusCode().is2xxSuccessful());

        //        3. 제목 / 내용 / 진행도 / 담당자 입력
        taskDto.setUserId(userId);
        ResponseEntity<TestTaskDto> response3 = restTemplate.postForEntity(url, entity, TestTaskDto.class);

        assert (response3.getStatusCode().is2xxSuccessful());
    }
}
