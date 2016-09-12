package com.jeckep.chat.util;

import com.jeckep.chat.user.User;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

//    public static String getQueryLocale(Request request) {
//        return request.queryParams("locale");
//    }

    public static String getQueryLoginRedirect(HttpServletRequest request) {
        return request.getParameter("loginRedirect");
    }

    public static String getSessionLocale(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("locale");
    }

    public static User getSessionCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

}
