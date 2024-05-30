package com.wicd.WhatIShouldDoApiServer.api.auth.controller;

import com.wicd.WhatIShouldDoApiServer.data.dto.auth.TestLoginDto;
import com.wicd.WhatIShouldDoApiServer.data.dto.auth.TestUserDto;
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

    @DisplayName("signup to authenticate test")
    @Test
    void signupTest() {
        String url = "http://localhost:" + randomServerPort + "/auth/signup";

        String username = RandomStringUtil.createRandomLengthEmailPatternString(5, 50);
        String password = RandomStringUtil.createRandomLengthString(3, 100);
        String nickname = RandomStringUtil.createRandomLengthString(3, 50);

        TestUserDto testCase = new TestUserDto(username, password, nickname);

//        1. 회원가입 테스트
//        1-1. 정상 가입
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

//        4. authentication (로그인) test
//        4-1. 정상 로그인
        String authenticateUrl = "http://localhost:" + randomServerPort + "/auth/authenticate";
        TestLoginDto loginDto = new TestLoginDto(username, password);
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(authenticateUrl, loginDto, Map.class);
        if (!loginResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("wrong login request : " + testCase.getUsername() + "/" + testCase.getPassword() + "/" + testCase.getNickname() + "/");
            System.out.println("wrong login result : " + response.getBody());
        }

        assert (loginResponse.getStatusCode().is2xxSuccessful());

        Map<String, String> loginResponseBody = loginResponse.getBody();
        assert (loginResponseBody.containsKey("accessToken")
                && loginResponseBody.containsKey("refreshToken")
                && !loginResponseBody.get("accessToken").isBlank()
                && !loginResponseBody.get("refreshToken").isBlank());

        //    4-2. 비밀번호 오류
        loginDto.setPassword(RandomStringUtil.createRandomLengthString(10, 20));

        ResponseEntity<Map> loginResponse2 = restTemplate.postForEntity(authenticateUrl, loginDto, Map.class);

        assert (loginResponse2.getStatusCode().is4xxClientError());

        //    4-2. 존재하지 않는 username 오류
        loginDto.setPassword(password);
        loginDto.setUsername(RandomStringUtil.createRandomLengthString(10, 20));

        ResponseEntity<Map> loginResponse3 = restTemplate.postForEntity(authenticateUrl, loginDto, Map.class);

        assert (loginResponse2.getStatusCode().is4xxClientError());
    }
}
