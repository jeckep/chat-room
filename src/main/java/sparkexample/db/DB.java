package sparkexample.db;

import java.util.List;

public interface DB {
    void createMessage(String message);
    List<Message> getAllMessages();
}
