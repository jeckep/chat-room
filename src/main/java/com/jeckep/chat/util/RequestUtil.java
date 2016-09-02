package com.jeckep.chat.util;

import spark.Request;

import javax.servlet.http.Cookie;

public class RequestUtil {

    public static String getQueryLocale(Request request) {
        return request.queryParams("locale");
    }

    public static String getQueryUsername(Request request) {
        return request.queryParams("username");
    }

    public static String getQueryPassword(Request request) {
        return request.queryParams("password");
    }

    public static String getQueryLoginRedirect(Request request) {
        return request.queryParams("loginRedirect");
    }

    public static String getSessionLocale(Request request) {
        return request.session().attribute("locale");
    }

    public static String getSessionCurrentUser(Request request) {
        return request.session().attribute("currentUser");
    }

    public static boolean removeSessionAttrLoggedOut(Request request) {
        Object loggedOut = request.session().attribute("loggedOut");
        request.session().removeAttribute("loggedOut");
        return loggedOut != null;
    }

    public static String removeSessionAttrLoginRedirect(Request request) {
        String loginRedirect = request.session().attribute("loginRedirect");
        request.session().removeAttribute("loginRedirect");
        return loginRedirect;
    }

    public static String getJsessionid(Request request){
        for(Cookie cookie: request.raw().getCookies()){
            if("JSESSIONID".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

}
