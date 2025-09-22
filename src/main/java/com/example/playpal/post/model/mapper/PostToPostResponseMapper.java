package com.example.playpal.post.model.mapper;

import com.example.playpal.auth.model.mapper.UserToUserResponseMapper;
import com.example.playpal.common.model.mapper.BaseMapper;
import com.example.playpal.game.model.mapper.GameToGameResponseMapper;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.dto.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        GameToGameResponseMapper.class,
        UserToUserResponseMapper.class,
})
public interface PostToPostResponseMapper extends BaseMapper<Post, PostResponse> {
    PostToPostResponseMapper INSTANCE = Mappers.getMapper(PostToPostResponseMapper.class);
}
