package com.example.springbackend.dto.update;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverUpdateDTO {
    private String username;
    private String name;
    private String surname;
    private String phoneNumber;
    private String city;
    private String profilePicture;
}
