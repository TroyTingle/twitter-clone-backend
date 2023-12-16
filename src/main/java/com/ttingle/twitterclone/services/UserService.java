package com.ttingle.twitterclone.services;

import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByID (String user_id) throws UserNotFoundException {
        UUID uuid = UUID.fromString(user_id);
        return userRepository.findById(uuid).orElseThrow(() -> new UserNotFoundException(user_id));
    }

}
