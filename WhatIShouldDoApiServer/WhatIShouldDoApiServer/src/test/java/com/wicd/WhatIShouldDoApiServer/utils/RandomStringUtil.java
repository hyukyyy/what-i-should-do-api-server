package com.wicd.WhatIShouldDoApiServer.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomStringUtil {
    public static String createLengthString(int randomStrLen) {
        boolean useLetters = true;
        boolean useNumbers = true;
        return RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
    }

    public static String createRandomLengthString(int from, int to) {
        int diff = to - from;
        int randomLen = from + (int) (Math.random() * diff);
        return createLengthString(randomLen);
    }

    public static String createRandomEmailPatternString(int randomStrLen) {
        // @ . random2 random3 최소값 보장을 위해 - 5
        randomStrLen = randomStrLen - 5;
        int random1 = (int) (Math.random() * randomStrLen) + 1;
        int randomStrLen2 = randomStrLen - random1;
        int random2 = (int) (Math.random() * randomStrLen2) + 1;
        int randomStrLen3 = randomStrLen2 - random2;
        int random3 = (int) (Math.random() * randomStrLen3) + 1;

        return createLengthString(random1)
                + "@"
                + createLengthString(random2)
                + "."
                + createLengthString(random3 + 1);
    }

    public static String createRandomLengthEmailPatternString(int from, int to) {
        int diff = to - from;
        int randomLen = from + (int) (Math.random() * diff);
        return createRandomEmailPatternString(randomLen);
    }
}
