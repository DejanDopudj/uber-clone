package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PassengerRide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double driverRating;
    private double vehicleRating;
    private String comment;
    private double price;
    
    @ManyToOne
    Passenger passenger;

    @ManyToOne
    Ride ride;
}
