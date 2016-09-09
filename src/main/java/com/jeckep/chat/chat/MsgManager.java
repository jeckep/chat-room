package com.jeckep.chat.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeckep.chat.Application;
import com.jeckep.chat.user.User;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.jeckep.chat.chat.ChatWebSocketHandler.liveSessions;
import static j2html.TagCreator.div;
import static j2html.TagCreator.img;

@Slf4j
public class MsgManager {
    private static final String COMMAND_LOAD_OLD = "LOAD_OLD_MESSAGES";
    private static final ObjectMapper mapper = new ObjectMapper();

    static WSMsg parse(String json) throws IOException {
       return mapper.readValue(json, WSMsg.class);
    }

    static void process(MsgWrapper msg, Session senderSession){
        if(COMMAND_LOAD_OLD.equals(msg.getMessage())){
            sendOldMessagesToLiveAgainUser(msg.getCurrentUser(), msg.getInterlocutor(), senderSession);
        }else{
            Application.msgDao.create(msg.getMsg());
            //echo to sender
            send(senderSession, msg);
            Session receiverSession = liveSessions.get(msg.getReceiver());
            if(receiverSession != null && msg.getSender() != msg.getReceiver()){
                //send to receiver
                // swap current user and interlocutor
                send(receiverSession, new MsgWrapper(msg.getMsg(), msg.getInterlocutor(), msg.getCurrentUser()));
            }
        }
    }

    private static void sendOldMessagesToLiveAgainUser(User currentUser, User interlocutor, Session session) {
        Application.msgDao.getAllMsgsFor(currentUser.getId(), interlocutor.getId()).stream()
                .sorted((a,b) -> a.getTs().compareTo(b.getTs()))
                .forEach( message -> send(session, new MsgWrapper(message, currentUser, interlocutor)));
    }

    private static void send(Session session, MsgWrapper msg){
        try {
            if(session.isOpen()){
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(msg))
                ));
            }
        } catch (Exception e) {
            log.error("Cannot process message from user:" + msg.getSender() + " to user: " + msg.getReceiver(), e);
        }
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(MsgWrapper msg) {
        final DateFormat df = new SimpleDateFormat("HH:mm dd.MM");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        final User sender = msg.curUsrIsSenderOfTheMsg()? msg.getCurrentUser() : msg.getInterlocutor();
        final String leftOrRight = msg.curUsrIsSenderOfTheMsg() ? "right" : "left";

        return
        div().withClass("answer " + leftOrRight).with(
            div().withClass("avatar").with(
                    img().withSrc(sender.getPicture()).withAlt(sender.getFullName())),
            div().withClass("name").withText(sender.getFullName()),
            div().withClass("text").withText(msg.getMessage()),
            div().withClass("time").withText(df.format(msg.getTs()))
        ).render();
    }
}
