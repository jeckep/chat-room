package com.jeckep.chat.util;

import com.jeckep.chat.model.User;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getSessionLocale(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("locale");
    }

    public static User getSessionCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

}
