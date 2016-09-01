package com.jeckep.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Msg {
    String sender;
    int from;
    int to;
    String message;
    Date ts;
}
