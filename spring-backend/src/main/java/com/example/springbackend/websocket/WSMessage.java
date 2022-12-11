package com.example.springbackend.websocket;

import com.example.springbackend.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public class WSMessage {
    @Getter
    private MessageType type;
    @Getter
    private String content;
    @Getter
    private String sender;
    @Getter
    private LocalDateTime sentDateTime;
}
