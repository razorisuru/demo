package com.hello.demo.entity;

import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator 
    private UUID id;

    @Column(unique = true)
    private String email;
    
    private String password;
    

    @Enumerated(EnumType.STRING)
    private Role role;
}