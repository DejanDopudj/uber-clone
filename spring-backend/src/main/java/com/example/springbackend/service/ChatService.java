package com.example.springbackend.service;

import com.example.springbackend.dto.display.ChatDisplayDTO;
import com.example.springbackend.model.Chat;
import com.example.springbackend.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springbackend.repository.ChatRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ChatDisplayDTO getUserChat(String username) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isPresent()) {
            Optional<Chat> chat = chatRepository.findByMember(u.get());
            if (chat.isPresent()) {
                return modelMapper.map(chat.get(), ChatDisplayDTO.class);
            }
            Chat newChat = new Chat();
            newChat.setMember(u.get());
            newChat.setLastReadAdmin(LocalDateTime.now());
            newChat.setLastReadMember(LocalDateTime.now());
            newChat.setMessages(new ArrayList<>());
            chatRepository.save(newChat);
            return modelMapper.map(newChat, ChatDisplayDTO.class);
        }
        return null;
    }

    public List<ChatDisplayDTO> getAllChats() {
        return Collections.singletonList(modelMapper.map(chatRepository.findAll(), ChatDisplayDTO.class));
    }
}
