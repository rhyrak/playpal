package com.example.playpal.unit.post.service.impl;

import com.example.playpal.auth.model.entity.UserEntity;
import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.common.exception.ForbiddenException;
import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.common.exception.UnprocessableContentException;
import com.example.playpal.common.security.UserType;
import com.example.playpal.common.util.AuthUtils;
import com.example.playpal.game.model.entity.GameEntity;
import com.example.playpal.game.repository.GameRepository;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.CreatePostRequest;
import com.example.playpal.post.model.entity.PostEntity;
import com.example.playpal.post.repository.PostRepository;
import com.example.playpal.post.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUtils authUtils;

    private final Long authenticatedUserId = 1L;


    @Test
    void givenValidRequest_whenCreatePost_thenPostIsSavedAndReturned() {
        // Given
        CreatePostRequest request = new CreatePostRequest(1L, "Test Description");

        // Mock
        GameEntity game = GameEntity.builder().id(request.getGameId()).build();
        UserEntity user = UserEntity.builder().id(authenticatedUserId).build();
        PostEntity savedPost = PostEntity.builder()
                .id(1L)
                .user(user)
                .game(game)
                .description(request.getDescription())
                .build();

        when(gameRepository.findById(request.getGameId())).thenReturn(Optional.of(game));
        when(authUtils.getAuthenticatedUserId()).thenReturn(authenticatedUserId);
        when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user));
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedPost);

        // When
        Post result = postService.createPost(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedPost.getId());
        assertThat(result.getUser().getId()).isEqualTo(savedPost.getUser().getId());
        assertThat(result.getGame().getId()).isEqualTo(request.getGameId());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());

        // Verify
        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void givenMissingGameIdInRequest_whenCreatePost_thenUnprocessableContentException() {
        // Given
        CreatePostRequest request = new CreatePostRequest(999L, "Test Description");

        // Mock
        when(gameRepository.findById(request.getGameId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnprocessableContentException.class, () -> postService.createPost(request));

        // Verify
        verify(gameRepository, times(1)).findById(request.getGameId());
        verify(userRepository, times(0)).findById(anyLong());
        verify(authUtils, times(0)).getAuthenticatedUserId();
        verify(postRepository, times(0)).save(any(PostEntity.class));
    }

    @Test
    void givenExistingPostId_whenGetPostById_thenReturnPost() {
        // Given
        Long postId = 1L;

        // Mock
        PostEntity post = PostEntity.builder().id(postId).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        Post result = postService.getPostById(postId);

        // Then
        assertThat(result.getId()).isEqualTo(postId);

        // Verify
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void givenNonExistentPostId_whenGetPostById_thenThrowResourceNotFound() {
        // Given
        Long postId = 1L;

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(postId));

        // Verify
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void whenGetAllPosts_thenReturnPostList() {
        // Mock
        var posts = List.of(PostEntity.builder().id(1L).build(), PostEntity.builder().id(2L).build());
        when(postRepository.findAll()).thenReturn(posts);

        // When
        List<Post> result = postService.getAllPosts();

        // Then
        assertThat(result).hasSize(2);
        assertEquals(posts.getFirst().getId(), result.getFirst().getId());

        // Verify
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void givenExistingPostIdAndAuthorizedUser_whenDeletePost_thenPostIsDeleted() {
        // Given
        Long postId = 1L;

        // Mock
        PostEntity postEntity = PostEntity.builder().id(postId)
                .user(UserEntity.builder().id(authenticatedUserId).build()).build();

        when(authUtils.getAuthenticatedUserId()).thenReturn(authenticatedUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        doNothing().when(postRepository).deleteById(postId);

        // When & Then
        assertThatNoException().isThrownBy(() -> postService.deletePost(postId));

        // Verify
        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void givenMissingPostId_whenDeletePost_thenThrowResourceNotFound() {
        // Given
        Long postId = 1L;

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(postId));

        // Verify
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(0)).deleteById(postId);
    }

    @Test
    void givenExistingPostIdAndUnauthorizedUser_whenDeletePost_thenThrowUnauthorized() {
        // Given
        Long postId = 1L;
        UserEntity unauthorizedUser = UserEntity.builder().id(authenticatedUserId)
                .userType(UserType.USER.getValue()).build();

        // Mock
        PostEntity postEntity = PostEntity.builder().id(postId)
                .user(UserEntity.builder().id(999L).build()).build();
        when(authUtils.getAuthenticatedUserId()).thenReturn(authenticatedUserId);
        when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(unauthorizedUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // When & Then
        assertThrows(ForbiddenException.class, () -> postService.deletePost(postId));

        // Verify
        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(0)).deleteById(postId);
    }

    @Test
    void givenExistingPostIdAndAdminUser_whenDeletePost_thenDeletePost() {
        // Given
        Long postId = 1L;
        Long postOwnerId = 2L;
        Long adminUserId = 3L;
        UserEntity adminUser = UserEntity.builder().id(adminUserId).userType(UserType.ADMIN.getValue()).build();

        // Mock
        PostEntity postEntity = PostEntity.builder().id(postId)
                .user(UserEntity.builder().id(postOwnerId).userType(UserType.USER.getValue()).build()).build();
        when(authUtils.getAuthenticatedUserId()).thenReturn(adminUserId);
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // When & Then
        assertThatNoException().isThrownBy(() -> postService.deletePost(postId));

        // Verify
        verify(authUtils, times(1)).getAuthenticatedUserId();
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }
}
