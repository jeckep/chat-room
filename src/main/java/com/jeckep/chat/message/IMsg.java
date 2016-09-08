package com.jeckep.chat.message;

public interface IMsg {
    int getId();

    int getSender();

    int getReceiver();

    String getMessage();

    java.util.Date getTs();
}
