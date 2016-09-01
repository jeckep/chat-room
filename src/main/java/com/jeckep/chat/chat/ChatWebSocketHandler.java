package com.jeckep.chat.chat;

import com.jeckep.chat.Application;
import com.jeckep.chat.message.Msg;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Date;

@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Application.nextUserNumber++;
        Application.userUsernameMap.put(user, username);
        Application.sendMessageHistoryToNewUser(user);
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

}
