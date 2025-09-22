package com.example.playpal.post.service;

import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.CreatePostRequest;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostRequest post);

    Post getPostById(Long id);

    List<Post> getAllPosts();

    void deletePost(Long id);
}
