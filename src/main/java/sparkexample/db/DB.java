package sparkexample.db;

import java.util.List;

/**
 * Created by jeck on 31.08.16.
 */
public interface DB {
    void createMessage(String message);
    List<Message> getAllMessages();
}
