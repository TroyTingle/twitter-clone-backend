package com.ttingle.twitterclone.controllers;


import com.ttingle.twitterclone.dto.CreatePostRequest;
import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.Post;
import com.ttingle.twitterclone.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createNewPost(@Valid @RequestBody CreatePostRequest createPostRequest) throws UserNotFoundException {
        return postService.createPost(createPostRequest);
    }

    @GetMapping("/all-posts")
    public ResponseEntity<List<Post>> getAllPosts(){
        return postService.getAllPosts();
    }

}
