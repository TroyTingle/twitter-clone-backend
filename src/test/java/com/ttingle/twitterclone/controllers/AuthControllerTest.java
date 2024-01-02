package com.ttingle.twitterclone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttingle.twitterclone.services.UserService;
import com.ttingle.twitterclone.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
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
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Mocking input data
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("validUsername", "validPassword");
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
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Mocking input data
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("invalidUsername", "invalidPassword");

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
    void signup_NewUser_ReturnsOk() throws Exception {
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
    void signup_ExistingUser_ReturnsConflict() throws Exception {
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

    // Utility method to convert an object to a JSON string
    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
