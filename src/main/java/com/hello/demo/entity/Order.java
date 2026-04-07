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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator // This tells Hibernate to generate the UUID
    private UUID id;
    
    private String item;
    private Double price;
}