package com.jeckep.chat.chat;

import com.jeckep.chat.Application;
import com.jeckep.chat.message.Msg;
import com.jeckep.chat.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {
    static Map<Integer, WebSocketSession> liveSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = resolveUser(session);
        liveSessions.put(user.getId(), session);
        //TODO process status changed to user with whom currentUser can talk
        log.info("User '" + user.getId() + "' is connected to chat websocket.");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            WSMsg msg = MsgManager.parse(message.getPayload());
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


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        User user = resolveUser(session);
        liveSessions.remove(user.getId());
        //TODO process status changed to user with whom currentUser was talking
        log.info("User '" + user.getId() + "' is disconnected from chat websocket.");
    }

    private static User resolveUser(WebSocketSession session){
        return (User) session.getAttributes().get("currentUser");
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
                    for(WebSocketSession session: liveSessions.values()){
                        if(session.isOpen()){
                            session.sendMessage(new PingMessage());
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
