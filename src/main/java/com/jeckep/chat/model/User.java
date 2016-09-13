package com.jeckep.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatuser")
public class User implements IUser, Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chatuser_id_seq")
    @SequenceGenerator(name = "chatuser_id_seq", sequenceName = "chatuser_id_seq", allocationSize = 1)
    Integer id;
    String name;
    String surname;
    String email;
    String picture;


    public User(String name, String surname, String email, String picture) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.picture = picture;
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getFullName(){
        return name + " " + surname;
    }
}
