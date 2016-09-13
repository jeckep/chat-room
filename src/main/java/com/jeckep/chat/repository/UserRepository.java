package com.jeckep.chat.repository;

import com.jeckep.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.id <> ?1")
    List<User> getAllUsersExcept(int userId);

    @Query("select u from User u where u.email = ?1")
    User findByEmail(String email);
}
