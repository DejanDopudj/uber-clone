package com.example.springbackend.dto.display;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideSimpleDisplayDTO {
    private Integer id;
    private Double distance;
    private LocalDateTime createdAt;
    private int price;
    private DriverSimpleDisplayDTO driver;
}
