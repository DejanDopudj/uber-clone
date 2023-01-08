package com.example.springbackend.service;
import com.example.springbackend.model.Message;
import com.example.springbackend.repository.ChatRepository;
import com.example.springbackend.repository.MessageRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.websocket.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;

    public void addMessage(WSMessage message) {
        Message newMessage = new Message();
        newMessage.setSender(userRepository.findByUsername(message.getSender()).get());
        String username = "";
        if (message.getSender().equals("admin")) {
            username = message.getReceiver();
        } else {
            username = message.getSender();
        }
        newMessage.setChat(chatRepository.findByMember(userRepository.findByUsername(username).get()).get());
        newMessage.setSentDateTime(message.getSentDateTime());
        newMessage.setContent(message.getContent());
        messageRepository.save(newMessage);
    }
}
