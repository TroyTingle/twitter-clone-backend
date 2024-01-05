package com.ttingle.twitterclone.services;

import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;



    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByID (Long user_id) throws UserNotFoundException {
        return userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id.toString()));
    }

    public User findByUsername (String username) throws UserNotFoundException{
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        List<GrantedAuthority> authorities =  new ArrayList<>(Collections.emptyList());
        authorities.add((GrantedAuthority) () -> user.getRole().name());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
