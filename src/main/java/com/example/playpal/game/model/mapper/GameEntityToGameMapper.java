package com.example.playpal.game.model.mapper;

import com.example.playpal.common.model.mapper.BaseMapper;
import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.entity.GameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameEntityToGameMapper extends BaseMapper<GameEntity, Game> {
    GameEntityToGameMapper INSTANCE = Mappers.getMapper(GameEntityToGameMapper.class);
}
