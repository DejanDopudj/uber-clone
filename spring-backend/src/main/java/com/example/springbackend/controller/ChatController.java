package com.example.springbackend.controller;

import com.example.springbackend.websocket.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;

    @Autowired
    ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/chat/sendMessage") // /app/chat/sendMessage
    @SendTo("/topic/public")
    public void sendMessage(String message) {
        System.out.println(message);
        this.template.convertAndSend("/topic/", message);
    }

    @MessageMapping("/chat/newUser")
    @SendTo("/topic/public")
    public WSMessage newUser(@Payload final WSMessage message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}
