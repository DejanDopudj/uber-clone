package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PassengerRide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int driverRating;
    private int vehicleRating;
    private String comment;
    private int fare;
    private boolean agreed;

    @ManyToOne
    Passenger passenger;

    @ManyToOne
    Ride ride;
}
