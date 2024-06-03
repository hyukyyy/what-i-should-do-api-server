package com.wicd.WhatIShouldDoApiServer.data.repository;

import com.wicd.WhatIShouldDoApiServer.data.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    Optional<Team> findByTeamId(@NonNull Long teamId);
}