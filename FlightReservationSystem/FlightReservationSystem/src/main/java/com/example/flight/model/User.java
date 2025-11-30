package com.example.flight.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    @Column(unique = true, nullable = false, length = 50)
    private String username;


    @Column(unique = true, nullable = false, length = 100)
    private String email;


    @Column(nullable = false)
    private String passwordHash;


    @Column(nullable = false)
    private String role = "user";


    private Instant createdAt = Instant.now();
}