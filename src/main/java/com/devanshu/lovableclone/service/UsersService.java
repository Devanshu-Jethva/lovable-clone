package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;

public interface UsersService {
    ProfileResponseDTO getProfile(Long userId);
}
