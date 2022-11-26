package com.example.springbackend.repository;

import com.example.springbackend.model.Admin;
import com.example.springbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String username);
}
