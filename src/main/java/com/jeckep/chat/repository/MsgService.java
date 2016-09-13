package com.jeckep.chat.repository;

import com.jeckep.chat.model.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MsgService {
    @Autowired
    private MsgRepository repository;

    public void save(Msg msg){
        repository.save(msg);
    }

    public List<Msg> getAllMsgsFor(int user1, int user2) {
        return repository.getAllMsgsFor(user1,user2);
    }
}
