package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String paypalOrderId;
    private String paypalOrderStatus;
    private int balance;
    @ManyToOne
    @JoinColumn(name = "passenger_id",referencedColumnName = "username")
    Passenger passenger;
}
