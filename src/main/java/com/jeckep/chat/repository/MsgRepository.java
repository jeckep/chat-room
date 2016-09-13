package com.jeckep.chat.repository;

import com.jeckep.chat.model.Msg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MsgRepository extends JpaRepository<Msg, Integer> {

    @Query("select m from Msg m where  (m.receiver=?1 and m.sender=?2) or (m.receiver=?2 and m.sender=?1)")
    List<Msg> getAllMsgsFor(int user1, int user2);
}
