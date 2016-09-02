package com.jeckep.chat;


import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.chatroom.ChatroomController;
import com.jeckep.chat.constants.Envs;
import com.jeckep.chat.index.IndexController;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.user.UserDao;
import com.jeckep.chat.util.Path;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import static spark.Spark.*;

@Slf4j
public class Application {
    public static UserDao userDao;

    private static final String DB_URL = System.getenv(Envs.DB_URL);

    public static void main(String[] args) {
        userDao = new UserDao();

        //migrateDB();

        staticFiles.location("/static");
        staticFiles.expireTime(600);
        webSocket("/chat/", ChatWebSocketHandler.class);
        init();

//        enableDebugScreen();


        get(Path.Web.CHAT_ROOM + "/:id",      ChatroomController.serveChatPage);
        get(Path.Web.INDEX,          IndexController.serveIndexPage);
        get(Path.Web.LOGIN,          LoginController.serveLoginPage);
        post(Path.Web.LOGIN,         LoginController.handleLoginPost);
        post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
//        get("*",                     ViewUtil.notFound);

        exception(Exception.class, (exception, request, response) -> {
            log.error("Unhandled exception",exception);
        });


    }

    private static void migrateDB(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB_URL, System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD));
        flyway.migrate();
    }
}