package com.ttingle.twitterclone.services;

import com.ttingle.twitterclone.dto.CreatePostRequest;
import com.ttingle.twitterclone.exceptions.UserNotFoundException;
import com.ttingle.twitterclone.model.Post;
import com.ttingle.twitterclone.model.User;
import com.ttingle.twitterclone.repositories.PostRepository;
import com.ttingle.twitterclone.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> createPost(CreatePostRequest createPostRequest) throws UserNotFoundException {
        //Get user from username
        User user = userRepository.findByUsername(createPostRequest.getPostUser()).orElseThrow(() -> new UserNotFoundException(createPostRequest.getPostUser()));
        //Create post and save
        Post newPost = new Post(createPostRequest.getPostContent(), user);
        postRepository.save(newPost);

        return new ResponseEntity<>("Post saved successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<Post>> getAllPosts(){
        return new ResponseEntity<>(postRepository.findAll(), HttpStatus.OK);
    }

}
