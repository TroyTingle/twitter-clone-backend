package com.ttingle.twitterclone.controllers;

import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserByID(@PathVariable String user_id) throws UserNotFoundException {
        User user = userService.findByID(user_id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }




}
