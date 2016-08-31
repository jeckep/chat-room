package com.jeckep.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Created by jeck on 31.08.16.
 */

@Data
@AllArgsConstructor
public class Message {
    String sender;
    String message;
    Date timestamp;
}
