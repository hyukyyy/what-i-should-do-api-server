package com.wicd.WhatIShouldDoApiServer.api.task.controller;

import com.wicd.WhatIShouldDoApiServer.data.dto.UserDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.task.TestTaskDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import com.wicd.WhatIShouldDoApiServer.data.repository.UserRepository;
import com.wicd.WhatIShouldDoApiServer.utils.RandomStringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

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
    Long userId;

    @BeforeEach
    public void createDummyUser() {
        userRepository.deleteAll();
        User user = userRepository.save(new UserDto(username, password, nickname));
        this.userId = user.getUserId();
    }

    @DisplayName("create task")
    @Test
    void createTaskTest() {
        String url = "http://localhost:" + port + "/task";

//        1. 제목 / 내용 입력
        String title = RandomStringUtil.createRandomLengthString(0, 30);
        String content = RandomStringUtil.createRandomLengthString(0, 3000);

        TestTaskDto taskDto = new TestTaskDto(title, content);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, taskDto, Map.class);

        assert (response.getStatusCode().is2xxSuccessful());

        //        2. 제목 / 내용 / 진행도 입력
        Integer progress = (int) (Math.random() * 100);

        taskDto.setProgress(progress);
        ResponseEntity<Map> response2 = restTemplate.postForEntity(url, taskDto, Map.class);

        assert (response2.getStatusCode().is2xxSuccessful());

        //        3. 제목 / 내용 / 진행도 / 담당자 입력
        taskDto.setUserId(userId);
        ResponseEntity<Map> response3 = restTemplate.postForEntity(url, taskDto, Map.class);

        assert (response2.getStatusCode().is2xxSuccessful());
    }
}
