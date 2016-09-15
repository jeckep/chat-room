package com.jeckep.chat.chat;

import com.jeckep.chat.domain.IMsg;
import com.jeckep.chat.domain.Msg;
import com.jeckep.chat.domain.User;

import java.util.Date;

public class MsgWrapper implements IMsg{
    private Msg msg;
    private User currentUser;
    private User interlocutor;

    public MsgWrapper(Msg msg, User currentUser, User interlocutor) {
        this.msg = msg;
        this.currentUser = currentUser;
        this.interlocutor = interlocutor;
    }

    @Override
    public Integer getId() {
        return msg.getId();
    }

    @Override
    public int getSender() {
        return msg.getSender();
    }

    @Override
    public int getReceiver() {
        return msg.getReceiver();
    }

    @Override
    public String getMessage() {
        return msg.getMessage();
    }

    @Override
    public Date getTs() {
        return msg.getTs();
    }

    public Msg getMsg() {
        return msg;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getInterlocutor() {
        return interlocutor;
    }

    public boolean curUsrIsSenderOfTheMsg(){
        return getCurrentUser().getId() == getSender();
    }
}
