package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String paypalOrderId;
    private String paypalOrderStatus;
    private int balance;
    private String username;
}
