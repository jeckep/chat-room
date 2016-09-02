package com.jeckep.chat.message;

import com.jeckep.chat.env.Envs;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class MsgDao {
    private static Sql2o sql2o;

    static{
        sql2o = new Sql2o(Envs.DB_URL, Envs.DB_USER, Envs.DB_PASSWORD);
    }

    public void create(Msg msg){
        String sql = "insert into message(sender, receiver, message, ts)" +
                     " values (:sender, :receiver, :message, :ts)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("sender", msg.getSender())
                    .addParameter("receiver",msg.getReceiver())
                    .addParameter("message", msg.getMessage())
                    .addParameter("ts", msg.getTs())
                    .executeUpdate();
        }
    }

    public List<Msg> getAllMsgsFor(int user1, int user2) {
        String sql = "select id, sender, receiver, message, ts" +
                " from message" +
                " where (receiver=:user1 and sender=:user2) or (receiver=:user2 and sender=:user1)" +
                " order by ts";
        try (Connection con = sql2o.open()){
            return con.createQuery(sql)
                    .addParameter("user1", user1)
                    .addParameter("user2", user2)
                    .executeAndFetch(Msg.class);
        }
    }
}
