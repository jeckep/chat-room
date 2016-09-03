package com.jeckep.chat.util;

import lombok.Getter;

public class Path {

    public static class Web {
        @Getter public static final String INDEX = "/";
        @Getter public static final String LOGIN = "/login/";
        @Getter public static final String LOGIN_AUTH2 = "/login/:service/";
        @Getter public static final String LOGIN_GOOGLE = "/login/google/";
        @Getter public static final String LOGIN_LINKEDIN = "/login/linkedin/";
        @Getter public static final String LOGIN_VK = "/login/vk/";
        @Getter public static final String LOGOUT = "/logout/";
        @Getter public static final String OAUTH2_CALLBACK = "/oauth2callback/:service/";
        @Getter public static final String CHAT_ROOM = "/chatroom/";
    }

    public static class Template {
        public static final String CHATROOM = "/velocity/chat/chatroom.html";
        public final static String INDEX = "/velocity/index/index.html";
        public final static String LOGIN = "/velocity/login/login.html";
        public static final String NOT_FOUND = "/velocity/notFound.html";
    }

}
