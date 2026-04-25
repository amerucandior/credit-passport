package com.passport.creditpassport.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id"),
        @UniqueConstraint(columnNames = "national_id")
        })
public class user {

    @GeneratedValue(strategy = GenerationType.UUID )
    @Id
    @Column(name = "user_id")
    private String id;

    @Column(name = "national_id", unique = true)
    private String natId;

    @Column(name = "user_name", unique = true)
    private String name;

    @Column(name = "user_no", unique = true)
    private String number;

    @Column(name = "password", nullable = false)
    private String password;
}
