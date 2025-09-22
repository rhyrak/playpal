package com.example.playpal.post.model.mapper;

import com.example.playpal.common.model.mapper.BaseMapper;
import com.example.playpal.game.model.mapper.GameEntityToGameMapper;
import com.example.playpal.post.model.Post;
import com.example.playpal.post.model.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        GameEntityToGameMapper.class,
})
public interface PostEntityToPostMapper extends BaseMapper<PostEntity, Post> {
    PostEntityToPostMapper INSTANCE = Mappers.getMapper(PostEntityToPostMapper.class);
}
