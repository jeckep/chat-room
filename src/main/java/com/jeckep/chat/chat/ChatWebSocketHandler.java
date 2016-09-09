package com.jeckep.chat.chat;

import com.github.jeckep.spark.PSF;
import com.jeckep.chat.Application;
import com.jeckep.chat.message.Msg;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@WebSocket
public class ChatWebSocketHandler {
    static Map<Integer, Session> liveSessions = new HashMap<>();

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
            User interlocutor = Application.userDao.getUserById(msg.getTo());
            //TODO check that current user can send messages to interlocutor

            log.info("Message from user:" + sender.getId() + " to user: " + msg.getTo() + " received and parsed.");
            MsgManager.process(
                    new MsgWrapper(
                            new Msg(sender.getId(), msg.getTo(), msg.getMessage()),
                            sender,
                            interlocutor),
                    session);
        } catch (IOException e) {
            log.error("Cannot parse web socket json message", e);
        }
    }

    private static User resolveUser(Session session){
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

    public static Set<Integer> getOnlineUserIds(){
        cleanSessions();
        return liveSessions.keySet();
    }

    private static synchronized void cleanSessions(){
        liveSessions = liveSessions.entrySet().stream()
                .filter(e -> e.getValue().isOpen())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // we need it to prevent websocket session expire and to prevent nginx to close proxy connection
    private static Thread pinger = new Thread(new KeepWebSocketSessionAlive());
    static {
        pinger.start();
    }
    private static class KeepWebSocketSessionAlive implements Runnable {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(50 * 1000);
                    cleanSessions();
                    for(Session session: liveSessions.values()){
                        if(session.isOpen()){
                            session.getRemote().sendPing(null);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("interrupted", e);
                } catch (IOException e) {
                    log.error("cannot send ping", e);
                }
            }
        }
    }
}
