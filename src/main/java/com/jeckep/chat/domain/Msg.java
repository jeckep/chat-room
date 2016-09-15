package com.jeckep.chat.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Msg implements IMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    @SequenceGenerator(name = "message_id_seq", sequenceName = "message_id_seq", allocationSize = 1)
    Integer id;
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
