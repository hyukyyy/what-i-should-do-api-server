package com.wicd.WhatIShouldDoApiServer.api.auth.controller;

import com.wicd.WhatIShouldDoApiServer.data.dto.TestUserDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.UserDto;
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
import org.springframework.test.annotation.Rollback;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
public class AuthControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    int randomServerPort;

    @BeforeEach
    public void clearUserTable() {
        userRepository.deleteAll();
    }

    @DisplayName("signup test")
    @Test
    void signupTest() {
        String url = "http://localhost:" + randomServerPort + "/auth/signup";

        String username = RandomStringUtil.createRandomLengthEmailPatternString(5, 50);
        String password = RandomStringUtil.createRandomLengthString(3, 50);
        String nickname = RandomStringUtil.createRandomLengthString(3, 50);

        TestUserDto testCase = new TestUserDto(username, password, nickname);

//        1. 정상 가입
        ResponseEntity<Map> response = restTemplate.postForEntity(url, testCase, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("wrong request : " + testCase.getUsername() + "/" + testCase.getPassword() + "/" + testCase.getNickname() + "/");
            System.out.println("wrong result : " + response.getBody());
        }

        assert (response.getStatusCode().is2xxSuccessful());

//        2. 중복 가입
        ResponseEntity<User> response2 = restTemplate.postForEntity(url, testCase, User.class);

        assert (response2.getStatusCode().is4xxClientError());

//        3. username 형식 오류
        testCase.setUsername(RandomStringUtil.createRandomLengthString(3, 50).replaceAll("@", ""));

        ResponseEntity<User> response3 = restTemplate.postForEntity(url, testCase, User.class);

        assert (response3.getStatusCode().is4xxClientError());
    }
}
