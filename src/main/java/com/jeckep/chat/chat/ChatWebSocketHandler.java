package com.jeckep.chat.chat;

import com.jeckep.chat.message.Msg;
import com.jeckep.chat.session.persist.PSF;
import com.jeckep.chat.user.User;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@WebSocket
public class ChatWebSocketHandler {
    public static Map<Integer, Session> liveSessions = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        User user = resolveUser(session);
        liveSessions.put(user.getId(), session);
        //TODO process status changed to user with whom currentUser can talk
        log.info("User '" + user.getId() + "' is connected to chat websocket.");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        User user = resolveUser(session);
        liveSessions.remove(user.getId());
        //TODO process status changed to user with whom currentUser was talking
        log.info("User '" + user.getId() + "' is disconnected from chat websocket.");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String json) {
        try {
            WSMsg msg = MsgManager.parse(json);
            User sender = resolveUser(session);
            log.info("Message from user:" + sender.getId() + " to user: " + msg.getTo() + " received and parsed.");
            MsgManager.process(new Msg(sender.getId(), msg.getTo(), msg.getMessage()), session);
        } catch (IOException e) {
            log.error("Cannot parse web socket json message", e);
        }
    }

    public static User resolveUser(Session session){
        String sessionCookie = getSessionCookie(session);
        if (sessionCookie == null){
            return null;
        }
        return AuthedUserListHolder.getInstance().getBySessionCookie(sessionCookie);
    }

    private static String getSessionCookie(Session session){
        for(HttpCookie cookie: session.getUpgradeRequest().getCookies()){
            if(PSF.SESSION_COOKIE_NAME.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

}
