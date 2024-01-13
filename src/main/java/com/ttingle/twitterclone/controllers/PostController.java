package com.ttingle.twitterclone.controllers;


import com.ttingle.twitterclone.dto.CreatePostRequest;
import com.ttingle.twitterclone.dto.PostDto;
import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<String> createNewPost(@Valid @RequestBody CreatePostRequest createPostRequest, @AuthenticationPrincipal UserDetails userDetails) throws UserNotFoundException {

        String username = userDetails.getUsername();
        if (!username.equals(createPostRequest.getPostUser())) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }

        return postService.createPost(createPostRequest);
    }

    @GetMapping("/all-posts")
    public ResponseEntity<List<PostDto>> getAllPosts(){
        return postService.getAllPosts();
    }

}
