package com.example.springbackend.controller;

import com.example.springbackend.dto.JwtAuthenticationRequestDTO;
import com.example.springbackend.model.User;
import com.example.springbackend.security.UserTokenState;
import com.example.springbackend.service.UserService;
import com.example.springbackend.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequestDTO authenticationRequest) {
            System.out.println(authenticationRequest.getUsername());
            System.out.println(authenticationRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            userService.processOAuthPostLogin(authenticationRequest.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String jwt = tokenUtils.generateToken(user.getUsername());
            int expiresIn = tokenUtils.getExpiredIn();
            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }
}
