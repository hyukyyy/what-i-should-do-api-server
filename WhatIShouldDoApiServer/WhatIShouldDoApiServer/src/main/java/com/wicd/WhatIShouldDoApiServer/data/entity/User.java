package com.wicd.WhatIShouldDoApiServer.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
//postgreSQL 기본 user table 과 중첩으로 user table은 이용불가
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(length = 100)
    private String password;

    @Column(length = 50)
    private String nickname;

    @JsonIgnore
    private boolean activated;

    @JsonIgnore
    private String refreshToken;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    @OneToMany(cascade = CascadeType.DETACH)
    private Set<Task> tasks;
}
