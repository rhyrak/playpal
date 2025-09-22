package com.example.playpal.auth.model.mapper;

import com.example.playpal.auth.model.User;
import com.example.playpal.auth.model.dto.UserResponse;
import com.example.playpal.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserToUserResponseMapper extends BaseMapper<User, UserResponse> {
    UserToUserResponseMapper INSTANCE = Mappers.getMapper(UserToUserResponseMapper.class);
}
