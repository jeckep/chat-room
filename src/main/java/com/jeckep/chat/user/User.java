package com.jeckep.chat.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class User implements IUser, Serializable{
    int id;
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
