package com.jeckep.chat.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeckep.chat.Application;
import com.jeckep.chat.message.Msg;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.jeckep.chat.chat.ChatWebSocketHandler.liveSessions;
import static j2html.TagCreator.*;

@Slf4j
public class MsgManager {
    private static final String COMMAND_LOAD_OLD = "LOAD_OLD_MESSAGES";
    private static final ObjectMapper mapper = new ObjectMapper();

    static WSMsg parse(String json) throws IOException {
       return mapper.readValue(json, WSMsg.class);
    }

    static void process(Msg msg, Session senderSession){
        if(COMMAND_LOAD_OLD.equals(msg.getMessage())){
            sendOldMessagesToLiveAgainUser(msg.getSender(), msg.getReceiver(), senderSession);
        }else{
            Application.msgDao.create(msg);
            //echo to sender
            send(senderSession, msg);
            Session receiverSession = liveSessions.get(msg.getReceiver());
            if(receiverSession != null && msg.getSender() != msg.getReceiver()){
                //send to receiver
                send(receiverSession, msg);
            }
        }
    }

    private static void sendOldMessagesToLiveAgainUser(int userFrom, int userTo, Session session) {
        Application.msgDao.getAllMsgsFor(userFrom, userTo).stream()
                .filter(msg ->
                        (msg.getReceiver() == userTo && msg.getSender() == userFrom)
                                || (msg.getReceiver() == userFrom && msg.getSender() == userTo))
                .sorted((a,b) -> a.getTs().compareTo(b.getTs()))
                .forEach( message -> send(session,message));
    }

    private static void send(Session session, Msg msg){
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(msg))
            ));
        } catch (Exception e) {
            log.error("Cannot process message sender user:" + msg.getSender() + " to user: " + msg.getReceiver(), e);
        }
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(Msg message) {
        return article().with(
                b(message.getSender() + " says:"),
                p(message.getMessage()),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(message.getTs()))
        ).render();
    }
}
