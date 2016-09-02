package com.jeckep.chat.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeckep.chat.message.Msg;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.jeckep.chat.chat.ChatWebSocketHandler.liveSessions;
import static j2html.TagCreator.*;

@Slf4j
public class MsgManager {
    private static final String COMMAND_LOAD_OLD = "LOAD_OLD_MESSAGES";
    private static List<Msg> messageHistory = new ArrayList<>();


    private static final ObjectMapper mapper = new ObjectMapper();

    static WSMsg parse(String json) throws IOException {
       return mapper.readValue(json, WSMsg.class);
    }

    static void process(Msg msg, Session senderSession){
        if(COMMAND_LOAD_OLD.equals(msg.getMessage())){
            sendOldMessagesToLiveAgainUser(msg.getFrom(), msg.getTo(), senderSession);
        }else{
            messageHistory.add(msg);
            //echo to sender
            send(senderSession, msg);
            Session receiverSession = liveSessions.get(msg.getTo());
            if(receiverSession != null){
                //send to receiver
                send(receiverSession, msg);
            }
        }
    }

    private static void sendOldMessagesToLiveAgainUser(int userFrom, int userTo, Session session) {
        messageHistory.stream()
                .filter(msg ->
                        (msg.getTo() == userTo && msg.getFrom() == userFrom)
                                || (msg.getTo() == userFrom && msg.getFrom() == userTo))
                .sorted((a,b) -> a.getTs().compareTo(b.getTs()))
                .forEach( message -> {
                    send(session,message);
                });
    }

    private static void send(Session session, Msg msg){
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(msg))
            ));
        } catch (Exception e) {
            log.error("Cannot process message from user:" + msg.getFrom() + " to user: " + msg.getTo(), e);
        }
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(Msg message) {
        return article().with(
                b(message.getFrom() + " says:"),
                p(message.getMessage()),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(message.getTs()))
        ).render();
    }
}
