package com.example.springbackend.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Admin extends User{
    @OneToMany(mappedBy = "admin")
    private List<Note> notes;
}
