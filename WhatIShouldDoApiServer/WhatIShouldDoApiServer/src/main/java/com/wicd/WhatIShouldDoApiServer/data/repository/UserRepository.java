package com.wicd.WhatIShouldDoApiServer.data.repository;

import com.wicd.WhatIShouldDoApiServer.data.dto.UserDto;
import com.wicd.WhatIShouldDoApiServer.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(UserDto user);

    Optional<User> findOneWithAuthoritiesByUsername(String username);

    Optional<User> findByUsername(@NonNull String username);

    void deleteAll();
}