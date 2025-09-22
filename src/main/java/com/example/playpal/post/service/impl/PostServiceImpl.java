package com.example.playpal.post.service.impl;

import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.common.exception.ForbiddenException;
import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.common.exception.UnprocessableContentException;
import com.example.playpal.common.security.UserType;
import com.example.playpal.common.util.AuthUtils;
import com.example.playpal.game.repository.GameRepository;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.CreatePostRequest;
import com.example.playpal.post.model.entity.PostEntity;
import com.example.playpal.post.model.mapper.PostEntityToPostMapper;
import com.example.playpal.post.repository.PostRepository;
import com.example.playpal.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    @Override
    public Post createPost(CreatePostRequest request) {
        var gameEntity = gameRepository.findById(request.getGameId());
        if (gameEntity.isEmpty())
            throw new UnprocessableContentException("Game not found");

        var userEntity = userRepository.findById(authUtils.getAuthenticatedUserId()).orElseThrow();
        
        var postEntity = PostEntity.builder()
                .game(gameEntity.get())
                .user(userEntity)
                .description(request.getDescription())
                .build();

        var savedPost = postRepository.save(postEntity);

        return PostEntityToPostMapper.INSTANCE.map(savedPost);
    }

    @Override
    public Post getPostById(Long id) {
        var postEntity = postRepository.findById(id).orElseThrow(postNotFound(id));
        return PostEntityToPostMapper.INSTANCE.map(postEntity);
    }

    @Override
    public List<Post> getAllPosts() {
        var postEntities = postRepository.findAll();
        return PostEntityToPostMapper.INSTANCE.map(postEntities);
    }

    @Override
    public void deletePost(Long id) {
        var postEntity = postRepository.findById(id).orElseThrow(postNotFound(id));

        // if authenticated user is not the owner
        var userId = authUtils.getAuthenticatedUserId();
        if (!postEntity.getUser().getId().equals(userId)) {
            var userEntity = userRepository.findById(userId).orElseThrow();
            // and not an admin
            if (!userEntity.getUserType().equals(UserType.ADMIN.getValue())) {
                // then forbidden
                throw new ForbiddenException("You are not authorized to delete this post");
            }
        }

        postRepository.deleteById(id);
    }


    private Supplier<ResourceNotFoundException> postNotFound(Long id) {
        return () -> new ResourceNotFoundException("Post with id " + id + " not found");
    }
}
