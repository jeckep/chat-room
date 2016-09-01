package com.jeckep.chat.chat;

import com.jeckep.chat.Application;
import com.jeckep.chat.login.AuthedUserListHolder;
import com.jeckep.chat.message.Msg;
import com.jeckep.chat.user.User;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.servlet.http.Cookie;
import java.net.HttpCookie;
import java.util.Date;

@Slf4j
@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        String username = "User" + Application.nextUserNumber++;
        User user = AuthedUserListHolder.getByJsessionid(getJsessionid(session));
        log.info("User '" + user.getUsername() + "' is connected to chat websocket.");
        Application.userUsernameMap.put(session, username);
        Application.sendMessageHistoryToNewUser(session);
        Application.broadcastMessage(new Msg("Server", 0,0, username + " joined the chat", new Date()));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Application.userUsernameMap.get(user);
        Application.userUsernameMap.remove(user);
        Application.broadcastMessage(new Msg("Server",0,0, username + " left the chat", new Date()));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // get current user from session
        // get toUser from session?
        // save message to db
        // send message to toUser if they are online
        // url for chat for user(id:1) talking to user(id:2) /chatroom/2
        Application.broadcastMessage(new Msg(Application.userUsernameMap.get(session),0,0,message, new Date()));
    }

    private String getJsessionid(Session session){
        for(HttpCookie cookie: session.getUpgradeRequest().getCookies()){
            if("JSESSIONID".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

}
