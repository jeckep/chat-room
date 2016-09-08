package com.jeckep.chat.chatroom;

import com.jeckep.chat.Application;
import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.user.User;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.RequestUtil;
import com.jeckep.chat.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ChatroomController {
    public static Route serveChatPage = (Request request, Response response) -> {
        String userToId = request.params("id");

        if(!LoginController.ensureUserIsLoggedIn(request, response)){
            //redirect to login
            return null;
        }
        final User currentUser = RequestUtil.getSessionCurrentUser(request);
        final List<User> users = getUserListFor(currentUser);

        final Set<Integer> onlineIds = users.stream().map(User::getId).collect(Collectors.toSet());
        onlineIds.retainAll(ChatWebSocketHandler.getOnlineUserIds());

        Map<String, Object> model = new HashMap<>();
        model.put("users", users);
        model.put("nav_active", "chatroom");
        model.put("userToId", userToId);
        model.put("onlineIds", onlineIds);
        return ViewUtil.render(request, model, Path.Template.CHATROOM);
    };

    private static List<User> getUserListFor(User currentUser){
        return Application.userDao.getAllUsersExcept(currentUser.getId());
    }
}
