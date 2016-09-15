package com.jeckep.chat.service;

import com.jeckep.chat.domain.IUser;
import com.jeckep.chat.domain.User;
import com.jeckep.chat.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<User> getAllUsersExcept(int userId){
        return userRepository.getAllUsersExcept(userId);
    }

    public User findOrCreate(IUser user){
        User foundUser = findByEmail(user.getEmail());
        if(foundUser == null){
            return create(user);
        }else{
            return foundUser;
        }
    }

    public User create(IUser user){
        return userRepository.saveAndFlush(new User(user.getName(), user.getSurname(), user.getEmail(), user.getPicture()));
    }


    public User findByEmail(String email){
       return userRepository.findByEmail(email);
    }

    public User getUserById(int id) {
        return userRepository.findOne(id);
    }
}
