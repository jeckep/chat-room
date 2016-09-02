package com.jeckep.chat.chatroom;

import com.jeckep.chat.Application;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;


public class ChatroomController {
    public static Route serveChatPage = (Request request, Response response) -> {
        String userToId = request.params("id");
        LoginController.ensureUserIsLoggedIn(request, response);
        Map<String, Object> model = new HashMap<>();
        model.put("users", Application.userDao.getAllUsers());
        model.put("nav_active", "chatroom");
        model.put("userToId", userToId);
//        model.put("toUserId", request.params("id"));
        return ViewUtil.render(request, model, Path.Template.CHATROOM);
    };
}
