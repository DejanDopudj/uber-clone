package com.example.springbackend.model;

import javax.persistence.*;

import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime sentDateTime;

    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id",referencedColumnName = "id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id",referencedColumnName = "username")
    private User sender;
}
