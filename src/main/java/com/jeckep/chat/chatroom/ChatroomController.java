package com.jeckep.chat.chatroom;

import com.jeckep.chat.Application;
import com.jeckep.chat.chat.ChatWebSocketHandler;
import com.jeckep.chat.login.LoginController;
import com.jeckep.chat.user.User;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.RequestUtil;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jeckep.chat.util.MvcUtil.redirect;

@Controller
public class ChatroomController {

    @GetMapping(Path.Web.CHAT_ROOM)
    public String serveChatPage(Map<String, Object> model, HttpServletRequest request){
        return serve(model, request, null);
    }

    @GetMapping(Path.Web.CHAT_ROOM + "{id}")
    public String serveChatPageWithMessages(Map<String, Object> model, HttpServletRequest request, @PathVariable("id") String userToId){
        return serve(model, request, userToId);
    }

    private String serve(Map<String, Object> model, HttpServletRequest request, @PathVariable("id") String userToId){
        if(!LoginController.ensureUserIsLoggedIn(request)){
            //redirect to login
            return redirect(Path.Web.LOGIN);
        }
        final User currentUser = RequestUtil.getSessionCurrentUser(request);
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

    private static List<User> getUserListFor(User currentUser){
        return Application.userDao.getAllUsersExcept(currentUser.getId());
    }
}
