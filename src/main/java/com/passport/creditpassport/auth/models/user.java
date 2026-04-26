package com.passport.creditpassport.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id"),
        @UniqueConstraint(columnNames = "national_id"),
        @UniqueConstraint(columnNames = "user_no")
        })
public class user {

    @GeneratedValue(strategy = GenerationType.UUID )
    @Id
    @Column(name = "user_id")
    private String id;

    @Column(name = "national_id", unique = true, nullable = false)
    private String natId;

    @Column(name = "user_name", unique = true, nullable = false)
    private String name;

    @Column(name = "user_no", unique = true, nullable = false)
    private String number;

    @Column(name = "password", nullable = false)
    private String password;
}
