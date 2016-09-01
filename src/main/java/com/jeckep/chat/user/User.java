package com.jeckep.chat.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    int id;
    String name;
    String surname;
    String email;

    //temp
    String username;
    String salt;
    String hashedPassword;

    public User(int id, String username, String salt, String hashedPassword) {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }
}
