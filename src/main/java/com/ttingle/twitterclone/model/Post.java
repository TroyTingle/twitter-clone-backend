package com.ttingle.twitterclone.model;

import jakarta.persistence.*;

@Entity
@Table(name="posts")
public class Post {

    @Id
    @GeneratedValue
    @Column(name="post_id")
    private Long postId;

    @Column(name="post_content")
    private String postContent;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User postUser;

    //Constructors
    public Post() {}
    public Post(String postContent, User postUser) {
        this.postContent = postContent;
        this.postUser = postUser;
    }

    //Getters and Setters
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

    public User getPostUser() {
        return postUser;
    }

    public void setPostUser(User postUser) {
        this.postUser = postUser;
    }
}
