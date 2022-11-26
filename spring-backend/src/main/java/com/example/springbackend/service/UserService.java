package com.example.springbackend.service;

import com.example.springbackend.model.User;
import com.example.springbackend.model.helpClasses.AuthenticationProvider;
import com.example.springbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public void processOAuthPostLogin(String email) {
        User existUser = userRepository.findByEmail(email);

        if (existUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
            userRepository.save(newUser);
        }
        System.out.println(email);
    }
}
