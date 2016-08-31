package sparkexample.db;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * Created by jeck on 31.08.16.
 */
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
            List<Message> messages = conn.createQuery("select * from message")
                    .executeAndFetch(Message.class);
            return messages;
        }
    }
}
