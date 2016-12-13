package com.jeckep.chat.domain.money;

import com.jeckep.chat.domain.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jeckep on 13.12.16.
 */

@Getter
@Setter
@Entity
@Table(name = Payment.TABLE)
public class Payment implements Serializable{
    public static final String TABLE = "payment";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Payment.TABLE + "_id_seq")
    @SequenceGenerator(name = Payment.TABLE + "_id_seq", sequenceName = Payment.TABLE + "_id_seq", allocationSize = 1)
    Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id")
    User user;
    long amount;
    Date ts;
    boolean paid;

    public Payment(int userId, long amount, boolean paid) {
        this.amount = amount;
        this.paid = paid;
        this.ts = new Date();
        this.user = User.id(userId);
    }
}
