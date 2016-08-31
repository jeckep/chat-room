package com.jeckep.chat.db;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;


public class DBImpl implements DB {
    private Sql2o sql2o;

    public DBImpl(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void createMessage(String message) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery("insert into message(message) VALUES (:message)")
                    .addParameter("message", message)
                    .executeUpdate();
            conn.commit();
        }
    }

    @Override
    public List getAllMessages() {
        try (Connection conn = sql2o.open()) {
            List<Msg> messages = conn.createQuery("select * from message")
                    .executeAndFetch(Msg.class);
            return messages;
        }
    }
}
