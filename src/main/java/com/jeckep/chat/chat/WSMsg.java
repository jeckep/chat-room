package com.jeckep.chat.chat;

import lombok.Data;

@Data
public class WSMsg {
    int to;
    String message;
}
