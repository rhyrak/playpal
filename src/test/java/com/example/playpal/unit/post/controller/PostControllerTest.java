package com.example.playpal.unit.post.controller;


import com.example.playpal.auth.model.User;
import com.example.playpal.common.exception.ForbiddenException;
import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.common.exception.UnprocessableContentException;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.CreatePostRequest;
import com.example.playpal.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    protected ObjectMapper objectMapper;

    private final List<Post> mockPosts = List.of(
            Post.builder().id(1L).user(User.builder().id(1L).build()).build(),
            Post.builder().id(2L).user(User.builder().id(2L).build()).build()
    );


    @Test
    public void givenValidCreatePostRequest_whenCreatePostAsGuest_thenUnauthorized() throws Exception {
        CreatePostRequest request = new CreatePostRequest(1L, "");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(postService, Mockito.never()).createPost(Mockito.any(CreatePostRequest.class));
    }

    @Test
    public void givenInvalidCreatePostRequest_whenCreatePostAsGuest_thenUnauthorized() throws Exception {
        CreatePostRequest request = new CreatePostRequest(-1L, "");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(postService, Mockito.never()).createPost(Mockito.any(CreatePostRequest.class));

    }

    @Test
    @WithMockUser
    public void givenInvalidCreatePostRequest_whenCreatePostAsUser_thenBadRequest() throws Exception {
        CreatePostRequest request = new CreatePostRequest(-1L, "");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(postService, Mockito.never()).createPost(Mockito.any(CreatePostRequest.class));
    }


    @Test
    @WithMockUser
    public void givenValidCreatePostRequest_whenCreatePostAsUser_thenCreated() throws Exception {
        CreatePostRequest request = new CreatePostRequest(1L, "");

        var savedPost = Post.builder().id(1L).build();
        Mockito.when(postService.createPost(request)).thenReturn(savedPost);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));

        Mockito.verify(postService, Mockito.times(1)).createPost(request);
    }

    @Test
    @WithMockUser
    public void givenCreatePostRequestWithMissingGameId_whenCreatePostAsUser_thenUnprocessableContent() throws Exception {
        CreatePostRequest request = new CreatePostRequest(999L, "");

        Mockito.when(postService.createPost(request)).thenThrow(UnprocessableContentException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(postService, Mockito.times(1)).createPost(request);
    }

    @Test
    public void whenGetAllPostsAsGuest_thenUnauthorized() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/posts")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(postService, Mockito.never()).getAllPosts();
    }

    @Test
    @WithMockUser
    public void whenGetAllPostsAsUser_thenSuccess() throws Exception {
        Mockito.when(postService.getAllPosts()).thenReturn(mockPosts);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/posts")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].user.id").value(2));

        Mockito.verify(postService, Mockito.times(1)).getAllPosts();
    }

    @Test
    public void givenPostId_whenGetPostById_thenUnauthorized() throws Exception {
        Long postId = 1L;
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(postService, Mockito.never()).getPostById(Mockito.anyLong());
    }


    @Test
    @WithMockUser
    public void givenExistingPostId_whenGetPostByIdAsUser_thenSuccess() throws Exception {
        Long postId = 1L;
        Mockito.when(postService.getPostById(postId)).thenReturn(mockPosts.getFirst());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").value(1));

        Mockito.verify(postService, Mockito.times(1)).getPostById(Mockito.anyLong());
    }

    @Test
    @WithMockUser
    public void givenNonExistentPostId_whenGetPostByIdAsUser_thenNotFound() throws Exception {
        Long postId = 1L;
        Mockito.when(postService.getPostById(postId))
                .thenThrow(new ResourceNotFoundException("Post with id " + postId + " does not exist"));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(postService, Mockito.times(1)).getPostById(Mockito.anyLong());
    }

    @Test
    @WithMockUser
    public void givenPostId_whenDeletePostByIdAsOwnerUser_thenSuccess() throws Exception {
        Long postId = 1L;

        Mockito.doNothing().when(postService).deletePost(postId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(postService, Mockito.times(1)).deletePost(Mockito.anyLong());
    }


    @Test
    @WithMockUser
    public void givenPostId_whenDeletePostByIdAsNonOwnerUser_thenForbidden() throws Exception {
        Long postId = 1L;

        Mockito.doThrow(ForbiddenException.class).when(postService).deletePost(postId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(postService, Mockito.times(1)).deletePost(Mockito.anyLong());
    }

    @Test
    @WithMockUser
    public void givenPostId_whenDeletePostByIdAsNonOwnerUser_thenNotFound() throws Exception {
        Long postId = 1L;

        Mockito.doThrow(ResourceNotFoundException.class).when(postService).deletePost(postId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/posts/{id}", postId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(postService, Mockito.times(1)).deletePost(Mockito.anyLong());
    }
}