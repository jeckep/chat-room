package com.jeckep.chat.message;

import lombok.Data;

import java.util.Date;

@Data
public class Msg {
    int id;
    int sender;
    int receiver;
    String message;
    Date ts;

    public Msg(int sender, int receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.ts = new Date();
    }
}
