package com.jeckep.chat.message;

import lombok.Data;

import java.util.Date;

@Data
public class Msg {
    int id;
    int from;
    int to;
    String message;
    Date ts;

    public Msg(int from, int to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.ts = new Date();
    }
}
