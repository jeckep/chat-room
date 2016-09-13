package com.jeckep.chat.model;

public interface IMsg {
    Integer getId();

    int getSender();

    int getReceiver();

    String getMessage();

    java.util.Date getTs();
}
