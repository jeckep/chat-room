package com.jeckep.chat.user;


import com.jeckep.chat.Application;

public class UserController {
    //mock accept all existed users without password
    //TODO create login via google
    public static boolean authenticate(String username, String password) {
        if (username.isEmpty()) {
            return false;
        }
        User user = Application.userDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        return true;
    }
}
