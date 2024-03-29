package com.ttingle.twitterclone.controllers;

import com.ttingle.twitterclone.dto.UpdatePasswordRequest;
import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.services.UserService;
import com.ttingle.twitterclone.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password));

            UserDetails userDetails = userService.loadUserByUsername(loginRequest.username);
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch(AuthenticationException authenticationException){
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid Login Credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignupRequest signupRequest){
        //Check if username already exists
        if (userService.existsByUsername(signupRequest.username)){
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        //Create new user
        User newUser = new User(signupRequest.username, signupRequest.emailAddress, passwordEncoder.encode(signupRequest.password));
        userService.saveUser(newUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Signup Successful!");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/update-password")
    public ResponseEntity<String> updatePassword(@PathVariable String username,
                                                 @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) throws UserNotFoundException {

        try{
            User user = userService.findByUsername(username);

            //Check old password matches the one in the database
            if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())){
                return new ResponseEntity<>("Old password does not match", HttpStatus.UNAUTHORIZED);
            }

            //Save new password to the database
            user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            userService.saveUser(user);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);

        } catch (UserNotFoundException e){

            return new ResponseEntity<>("No user with username: " + username, HttpStatus.NOT_FOUND);
        }
    }

    public record LoginRequest(String username, String password){}
    public record SignupRequest(String emailAddress, String username, String password){}

}
