package com.devanshu.lovableclone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;
import com.devanshu.lovableclone.dto.auth.UserProfileDTO;
import com.devanshu.lovableclone.entity.Users;

@Mapper(componentModel = "spring")
public interface UsersMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "stripeCustomerId", ignore = true)
	Users toEntity(SignupRequestDTO signupRequestDTO);

	UserProfileDTO toDto(Users users);

	ProfileResponseDTO toProfileResponseDTO(Users users);
}
