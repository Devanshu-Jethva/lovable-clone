package com.devanshu.lovableclone.mapper;

import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;
import com.devanshu.lovableclone.dto.auth.UserProfileDTO;
import com.devanshu.lovableclone.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    Users toEntity(SignupRequestDTO signupRequestDTO);

    UserProfileDTO toDto(Users users);
}
