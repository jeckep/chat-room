package com.jeckep.chat.controller;

import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.model.User;
import com.jeckep.chat.repository.UserService;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.RequestUtil;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ChatroomController {
    @Autowired
    private UserService userService;

    @GetMapping(Path.Web.CHAT_ROOM)
    public String serveChatPage(Map<String, Object> model, HttpServletRequest request){
        return serve(model, request, null);
    }

    @GetMapping(Path.Web.CHAT_ROOM + "{id}")
    public String serveChatPageWithMessages(Map<String, Object> model, HttpServletRequest request, @PathVariable("id") String userToId){
        return serve(model, request, userToId);
    }

    private String serve(Map<String, Object> model, HttpServletRequest request, @PathVariable("id") String userToId){
        final User currentUser = RequestUtil.getCurrentUser();
        final List<User> users = getUserListFor(currentUser);

        final Set<Integer> onlineIds = users.stream().map(User::getId).collect(Collectors.toSet());
        onlineIds.retainAll(ChatWebSocketHandler.getOnlineUserIds());

        model.put("users", users);
        model.put("nav_active", "chatroom");
        model.put("userToId", userToId);
        model.put("onlineIds", onlineIds);

        ViewUtil.putLayoutVars(request, model);
        return Path.Template.CHATROOM;
    }

    private List<User> getUserListFor(User currentUser){
        return userService.getAllUsersExcept(currentUser.getId());
    }
}
