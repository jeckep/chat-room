package com.jeckep.chat.chatroom;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;


public class ChatroomController {
    public static Route serveChatPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, Path.Template.CHATROOM);
    };
}
