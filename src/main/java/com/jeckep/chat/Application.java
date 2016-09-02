package com.jeckep.chat;


import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.chatroom.ChatroomController;
import com.jeckep.chat.env.Envs;
import com.jeckep.chat.index.IndexController;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.message.MsgDao;
import com.jeckep.chat.user.UserDao;
import com.jeckep.chat.util.Filters;
import com.jeckep.chat.util.Path;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import static spark.Spark.*;

@Slf4j
public class Application {
    public static UserDao userDao;
    public static MsgDao msgDao;

    public static void main(String[] args) {
        userDao = new UserDao();
        msgDao = new MsgDao();
        migrateDB();

        staticFiles.location("/static");
        staticFiles.expireTime(600);
        webSocket("/chat/", ChatWebSocketHandler.class);
        init();

//        enableDebugScreen();
        before("*",                  Filters.handleLocaleChange);

        get(Path.Web.CHAT_ROOM + "/:id",      ChatroomController.serveChatPage);
        get(Path.Web.INDEX,          IndexController.serveIndexPage);
        get(Path.Web.LOGIN,          LoginController.serveLoginPage);
        post(Path.Web.LOGIN,         LoginController.handleLoginPost);
        post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
//        get("*",                     ViewUtil.notFound);

        //Set up after-filters (called after each get/post)
        after("*",                   Filters.addGzipHeader);

        exception(Exception.class, (exception, request, response) -> {
            log.error("Unhandled exception",exception);
        });
    }

    private static void migrateDB(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(Envs.DB_URL, Envs.DB_USER, Envs.DB_PASSWORD);
        flyway.migrate();
    }
}