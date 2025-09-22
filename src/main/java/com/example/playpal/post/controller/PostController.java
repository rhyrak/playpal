package com.example.playpal.post.controller;

import com.example.playpal.common.docs.UnauthorizedApiResponse;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.CreatePostRequest;
import com.example.playpal.post.model.dto.PostResponse;
import com.example.playpal.post.model.mapper.PostToPostResponseMapper;
import com.example.playpal.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER)
@SecurityRequirement(name = "Bearer")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post"
    )
    @ApiResponse(responseCode = "201", description = "Post successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "422", description = "No such game exists with given game id")
    @UnauthorizedApiResponse
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPost(@RequestBody @Valid CreatePostRequest post) {
        Post createdPost = postService.createPost(post);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPost.getId()).toUri()).build();
    }


    @Operation(
            summary = "Get a post",
            description = "Returns post details"
    )
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Post with given id does not exist")
    @UnauthorizedApiResponse
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        var post = postService.getPostById(id);
        return ResponseEntity.ok(PostToPostResponseMapper.INSTANCE.map(post));
    }


    @Operation(
            summary = "Get all post",
            description = "Returns all post details"
    )
    @ApiResponse(responseCode = "200", description = "Success")
    @UnauthorizedApiResponse
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        var posts = postService.getAllPosts();
        return ResponseEntity.ok(PostToPostResponseMapper.INSTANCE.map(posts));
    }

    @Operation(
            summary = "Delete a post",
            description = "Deletes post with given id"
    )
    @ApiResponse(responseCode = "204", description = "Post is deleted")
    @ApiResponse(responseCode = "404", description = "Post with given id does not exist")
    @UnauthorizedApiResponse
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
