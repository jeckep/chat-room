package com.jeckep.chat;


import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.chatroom.ChatroomController;
import com.jeckep.chat.constants.Envs;
import com.jeckep.chat.index.IndexController;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.message.Msg;
import com.jeckep.chat.user.UserDao;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.flywaydb.core.Flyway;
import org.json.JSONObject;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;
import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

@Slf4j
public class Application {
    public static UserDao userDao;

    public static Map<Session, String> userUsernameMap = new HashMap<>();
    public static int nextUserNumber = 1; //Assign to username for next connecting user
    public static List<Msg> messageHistory = new ArrayList<>();
    private static final String DB_URL = System.getenv(Envs.DB_URL);

    public static void main(String[] args) {
        userDao = new UserDao();

        //migrateDB();

        staticFiles.location("/static");
        staticFiles.expireTime(600);
        webSocket("/chat/", ChatWebSocketHandler.class);
        init();

//        enableDebugScreen();


        get(Path.Web.CHAT_ROOM,      ChatroomController.serveChatPage);
        get(Path.Web.INDEX,          IndexController.serveIndexPage);
        get(Path.Web.LOGIN,          LoginController.serveLoginPage);
        post(Path.Web.LOGIN,         LoginController.handleLoginPost);
        post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
//        get("*",                     ViewUtil.notFound);

    }






    private static void migrateDB(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB_URL, System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD));
        flyway.migrate();
    }


    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(Msg message) {
        messageHistory.add(message);
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(message))
                        .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(Msg message) {
        return article().with(
                b(message.getFrom() + " says:"),
                p(message.getMessage()),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(message.getTs()))
        ).render();
    }

    public static void sendMessageHistoryToNewUser(Session user) {
        messageHistory.stream().sorted((a,b) -> a.getTs().compareTo(b.getTs())).forEach( message -> {
            try {
                user.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(message))
                        .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}