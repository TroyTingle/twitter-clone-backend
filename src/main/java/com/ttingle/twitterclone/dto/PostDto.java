package com.ttingle.twitterclone.dto;

public class PostDto {

    private Long postId;

    private String postContent;

    private String username;

    //Constructors
    @SuppressWarnings("unused")
    public PostDto() {}

    public PostDto(Long postId, String postContent, String username) {
        this.postId = postId;
        this.postContent = postContent;
        this.username = username;
    }

    //Getters and setters
    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public String getPostContent() {
        return postContent;
    }
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
