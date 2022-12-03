package com.example.springbackend.model;

import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Member extends User{

    Boolean blocked;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<Note> notes;
}
