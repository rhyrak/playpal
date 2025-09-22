package com.example.playpal.auth.model.mapper;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.TokenResponse;
import com.example.playpal.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenPairToTokenResponseMapper extends BaseMapper<TokenPair, TokenResponse> {
    TokenPairToTokenResponseMapper INSTANCE = Mappers.getMapper(TokenPairToTokenResponseMapper.class);
}
