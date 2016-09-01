package com.jeckep.chat.user;


import static com.jeckep.chat.Application.userDao;

public class UserController {
    //mock accept all existed users without password
    public static boolean authenticate(String username, String password) {
        if (username.isEmpty()) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        return true;
    }
}
