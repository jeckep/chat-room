package com.jeckep.chat.util;

import lombok.Getter;

public class Path {

    public static class Web {
        @Getter public static final String INDEX = "/";
        @Getter public static final String LOGIN = "/loginpage/";
        @Getter public static final String LOGIN_AUTH2 = "/login/{service}/";
        @Getter public static final String LOGIN_GOOGLE = "/login/google/";
        @Getter public static final String LOGIN_LINKEDIN = "/login/linkedin/";
        @Getter public static final String LOGIN_GITHUB= "/login/github/";
        @Getter public static final String LOGIN_VK = "/login/vk/";
        @Getter public static final String LOGIN_FB = "/login/fb/";
        @Getter public static final String LOGOUT = "/logout";
        @Getter public static final String OAUTH2_CALLBACK = "/oauth2callback/{service}/";
        @Getter public static final String CHAT_ROOM = "/chatroom/";
        @Getter public static final String CONTACT = "/contact/";
    }

    public static class Template {
        public static final String CHATROOM = "chat/chatroom.html";
        public final static String INDEX = "index/index.html";
        public final static String LOGIN = "login/login.html";
        public static final String NOT_FOUND = "notFound.html";
        public static final String CONTACT = "contact.html";
    }

}
