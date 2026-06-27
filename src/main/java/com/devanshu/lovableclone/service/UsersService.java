package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.entity.Users;

public interface UsersService {
    ProfileResponseDTO getProfile(Long userId);

    Users getUsers(Long id);
}
