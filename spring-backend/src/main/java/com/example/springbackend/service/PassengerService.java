package com.example.springbackend.service;

import com.example.springbackend.model.Passenger;
import com.example.springbackend.model.User;
import com.example.springbackend.model.helpClasses.AuthenticationProvider;
import com.example.springbackend.model.security.CustomOAuth2User;
import com.example.springbackend.repository.PassengerRepository;
import com.example.springbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private UserRepository userRepository;


    public void processOAuthPostLogin(CustomOAuth2User customOAuth2User) {
        Passenger existUser = passengerRepository.findByEmail(customOAuth2User.getEmail());

        if (existUser == null) {
            Passenger newUser = new Passenger();
            newUser.setEmail(customOAuth2User.getEmail());
            newUser.setAuthenticationProvider(AuthenticationProvider.valueOf(customOAuth2User.getOauth2ClientName().toUpperCase()));
            passengerRepository.save(newUser);
        }

    }
}
