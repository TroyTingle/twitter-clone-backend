package com.ttingle.twitterclone.controllers;

import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByID(@PathVariable String username) throws UserNotFoundException {
        User user = userService.findByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
