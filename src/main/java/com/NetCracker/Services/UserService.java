package com.NetCracker.Services;


import com.NetCracker.Entities.User;
import com.NetCracker.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id){

        log.info("IN UserServive getById {}",id);
        return userRepository.findById(id).get();
    }

    public List<User> findAll(){
        log.info("IN UserServive findAll");
        return userRepository.findAll();
    }

    public User saveUser(User user){
        log.info("IN UserServive save {}", user);
        return userRepository.save(user);
    }

    public void deleteById(Long id){
        log.info("IN UserServive delete{}", id);
        userRepository.deleteById(id);
    }
}