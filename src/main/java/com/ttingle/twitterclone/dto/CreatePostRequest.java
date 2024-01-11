package com.ttingle.twitterclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotBlank(message = "User ID cannot be blank")
    private String postUser;

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 10, max = 255, message = "Post must be between 10 and 255 characters")
    private String postContent;

    //Constructors
    public CreatePostRequest() {}

    public CreatePostRequest(String postUserId, String postContent) {
        this.postUser = postUserId;
        this.postContent = postContent;
    }

    //Getters
    public String getPostUser() {
        return postUser;
    }
    public String getPostContent() {
        return postContent;
    }
}
