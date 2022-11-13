package com.example.springbackend.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "member_username")
    Member member;

    private LocalDateTime lastReadAdmin;

    private LocalDateTime lastReadMember;
}
