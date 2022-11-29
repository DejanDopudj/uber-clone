package com.example.springbackend.service;

import com.example.springbackend.model.Member;
import com.example.springbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TestDataSupplierService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public void injectTestData() {
        addUsers();
    }

    private void addUsers() {
        Member member = new Member();
        member.setUsername("member1");
        member.setEmail("member1@noemail.com");
        member.setPassword("cascaded");
        member.setName("Membrane");
        member.setSurname("Memphis");
        member.setPhoneNumber("06146014691");
        member.setCity("Novi Sad");
        member.setBlocked(false);
        userRepository.save(member);
    }
}
