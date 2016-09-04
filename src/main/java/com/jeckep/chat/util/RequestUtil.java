package com.jeckep.chat.util;

import com.jeckep.chat.user.User;
import spark.Request;

public class RequestUtil {

    public static String getQueryLocale(Request request) {
        return request.queryParams("locale");
    }

    public static String getQueryLoginRedirect(Request request) {
        return request.queryParams("loginRedirect");
    }

    public static String getSessionLocale(Request request) {
        return request.session().attribute("locale");
    }

    public static User getSessionCurrentUser(Request request) {
        return request.session().attribute("currentUser");
    }

}
