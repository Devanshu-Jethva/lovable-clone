package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.entity.Users;

public interface UsersService {
	ProfileResponseDTO getProfile();

	Users getUsers(Long id);
}
