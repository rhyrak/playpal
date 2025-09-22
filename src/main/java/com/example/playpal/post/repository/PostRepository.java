package com.example.playpal.post.repository;

import com.example.playpal.post.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

//    @Query(value = " SELECT " +
//            "  plp_posts.id, plp_posts.description, plp_users.id, plp_users.username, plp_games.id, plp_games.title " +
//            "FROM plp_posts " +
//            "  JOIN plp_games on plp_games.id = plp_posts.game_id " +
//            "  JOIN plp_users on plp_users.id = plp_posts.user_id " +
//            "WHERE plp_posts.id = :postId", nativeQuery = true)
//    Optional<PostDetailsDTO> getPostDetails(@Param("postId") Long postId);
//
//    @Query(value = " SELECT " +
//            "  plp_posts.id, plp_posts.description, plp_users.id, plp_users.username, plp_games.id, plp_games.title " +
//            "FROM plp_posts " +
//            "  JOIN plp_games on plp_games.id = plp_posts.game_id " +
//            "  JOIN plp_users on plp_users.id = plp_posts.user_id", nativeQuery = true)
//    List<PostDetailsDTO> getAllPostDetails();
}
