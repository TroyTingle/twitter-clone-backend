package com.ttingle.twitterclone.controllers;

import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.services.UserService;
import com.ttingle.twitterclone.util.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password));

        UserDetails userDetails = userService.loadUserByUsername(loginRequest.username);
        String token = jwtTokenUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
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

    public record LoginRequest(String username, String password){}
    public record SignupRequest(String emailAddress, String username, String password){}


}
