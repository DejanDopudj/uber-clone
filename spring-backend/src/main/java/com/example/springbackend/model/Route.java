package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}
