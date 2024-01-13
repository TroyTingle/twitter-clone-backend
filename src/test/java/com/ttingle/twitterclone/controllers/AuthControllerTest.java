package com.ttingle.twitterclone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttingle.twitterclone.dto.LoginRequest;
import com.ttingle.twitterclone.dto.UpdatePasswordRequest;
import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.services.UserService;
import com.ttingle.twitterclone.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginValidCredentialsReturnsToken() throws Exception {
        // Mocking input data
        LoginRequest loginRequest = new LoginRequest("validUsername", "validPassword");
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        // Mocking authenticationManager behavior
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userService.loadUserByUsername("validUsername")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("validToken");

        // Setting up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Performing the request
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .content(asJsonString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("validToken"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void loginInvalidCredentialsReturnsUnauthorized() throws Exception {
        // Mocking input data
        LoginRequest loginRequest = new LoginRequest("validUsername", "validPassword");

        // Mocking authenticationManager behavior to throw AuthenticationException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {
                });

        // Setting up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Performing the request
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .content(asJsonString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid Login Credentials"));
    }

    @Test
    void signupNewUserReturnsOk() throws Exception {
        // Mocking input data
        AuthController.SignupRequest signupRequest = new AuthController.SignupRequest("test@email.com", "newUser", "password");

        // Mocking userService behavior
        when(userService.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Setting up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Performing the request
        ResultActions result = mockMvc.perform(put("/api/auth/signup")
                .content(asJsonString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Signup Successful!"));
    }

    @Test
    void signupExistingUserReturnsConflict() throws Exception {
        // Mocking input data
        AuthController.SignupRequest signupRequest = new AuthController.SignupRequest("test@email.com", "existingUser", "password");

        // Mocking userService behavior
        when(userService.existsByUsername("existingUser")).thenReturn(true);

        // Setting up MockMvc
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Performing the request
        ResultActions result = mockMvc.perform(put("/api/auth/signup")
                .content(asJsonString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void updatePasswordReturnsOk() throws UserNotFoundException{
        String username = "testUser";
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");
        User user = new User(username, "test@email.com", "oldPassword");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password", new ArrayList<>(Collections.emptyList()));

        when(userService.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);

        ResponseEntity<String> response = authController.updatePassword(username, request, userDetails);

        verify(userService, times(1)).saveUser(any(User.class));
        verify(passwordEncoder, times(1)).encode(request.getNewPassword());

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals("Password updated successfully", response.getBody())
        );
    }

    @Test
    void updatePasswordOldPasswordMismatchReturnsUnauthorized() throws UserNotFoundException {
        String username = "testUser";
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");
        User user = new User(username,"test@email.com", "differentPassword");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password", new ArrayList<>(Collections.emptyList()));

        when(userService.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);


        ResponseEntity<String> response = authController.updatePassword(username, request, userDetails);

        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertEquals("Old password does not match", response.getBody())
        );

        verify(userService, never()).saveUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updatePasswordUnauthorizedUserReturnsUnauthorized() throws UserNotFoundException {
        String username = "testUser";
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");
        User user = new User(username,"test@email.com", "differentPassword");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testUser2", "password", new ArrayList<>(Collections.emptyList()));

        ResponseEntity<String> response = authController.updatePassword(username, request, userDetails);

        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertEquals("Unauthorized Request", response.getBody())
        );

        verify(userService, never()).saveUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updatePasswordUserNotFoundReturnsNotFound() throws UserNotFoundException {
        String username = "nonexistentUser";
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password", new ArrayList<>(Collections.emptyList()));

        when(userService.findByUsername(username)).thenThrow(UserNotFoundException.class);

        ResponseEntity<String> response = authController.updatePassword(username, request, userDetails);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertTrue(response.getBody().contains("No user with username:"))
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userService, never()).saveUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Utility method to convert an object to a JSON string
    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
