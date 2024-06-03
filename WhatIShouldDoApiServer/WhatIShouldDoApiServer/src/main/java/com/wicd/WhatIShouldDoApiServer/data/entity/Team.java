package com.wicd.WhatIShouldDoApiServer.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_team",
            joinColumns = {@JoinColumn(name = "team_id", referencedColumnName = "team_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")})
    private Set<User> users;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks;
}
