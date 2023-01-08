package com.example.springbackend.controller;

import com.example.springbackend.websocket.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WSChatController {

    private final SimpMessagingTemplate template;

    @Autowired
    WSChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/chat/publicMessage") // /app/chat/publicMessage
    @SendTo("/topic/public")               // primjer za globalno obavjestenje
    public void sendMessage(String message) {
        this.template.convertAndSend("/topic/", message);
    }

    @MessageMapping("/privateMessage") // /app/privateMessage
    public WSMessage recMessage(@Payload final WSMessage message) {
        this.template.convertAndSendToUser(message.getReceiver(), "/private", message);
        System.out.println(message.toString());
        return message;
    }
}
