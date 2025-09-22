package com.example.playpal.auth.model.mapper;

import com.example.playpal.auth.model.entity.UserEntity;
import com.example.playpal.common.model.mapper.BaseMapper;
import com.example.playpal.common.security.JwtClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserEntityToClaimsMapper extends BaseMapper<UserEntity, Claims> {
    UserEntityToClaimsMapper INSTANCE = Mappers.getMapper(UserEntityToClaimsMapper.class);

    @Override
    default Claims map(UserEntity source) {
        return Jwts.claims()
                .subject(source.getUsername())
                .add(JwtClaims.USER_ID.getValue(), source.getId())
                .add(JwtClaims.USERNAME.getValue(), source.getUsername())
                .add(JwtClaims.USER_TYPE.getValue(), source.getUserType())
                .build();
    }
}
