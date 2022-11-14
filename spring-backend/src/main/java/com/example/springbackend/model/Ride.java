package com.example.springbackend.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double distance;
    private String cancelled;
    private Boolean rejected;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean driverInconsistency;
    private Double price;

    @ManyToOne
    @JoinColumn
    private Route actualRoute;

    @ManyToOne
    @JoinColumn
    private Route expectedRoute;


    @ManyToMany()
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "passenger_rides",
            joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passanger_username", referencedColumnName = "username"))
    private List<Passenger> passengers;
}
