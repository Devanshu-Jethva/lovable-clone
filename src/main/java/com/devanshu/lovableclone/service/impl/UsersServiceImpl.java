package com.devanshu.lovableclone.service.impl;

import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.mapper.UsersMapper;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.UsersService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	HelperService helperService;
	UsersMapper usersMapper;

	@Override
	public ProfileResponseDTO getProfile() {
		JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
		Long userId = jwtUserPrincipal.id();
		return usersMapper.toProfileResponseDTO(getUsers(userId));
	}

	@Override
	public Users getUsers(final Long id) {
		return usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
	}

}
