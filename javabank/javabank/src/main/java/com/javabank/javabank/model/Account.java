package com.javabank.javabank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private Double balance = 0.0;
}
