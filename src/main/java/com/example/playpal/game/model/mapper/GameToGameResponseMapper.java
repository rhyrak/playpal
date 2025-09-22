package com.example.playpal.game.model.mapper;

import com.example.playpal.common.model.mapper.BaseMapper;
import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.dto.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameToGameResponseMapper extends BaseMapper<Game, GameResponse> {
    GameToGameResponseMapper INSTANCE = Mappers.getMapper(GameToGameResponseMapper.class);
}
