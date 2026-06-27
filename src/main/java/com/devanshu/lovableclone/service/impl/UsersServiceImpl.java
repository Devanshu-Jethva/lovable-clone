package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.service.UsersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsersServiceImpl implements UsersService {

    UsersRepository usersRepository;

    @Override
    public ProfileResponseDTO getProfile(Long userId) {
        return null;
    }

    @Override
    public Users getUsers(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

}
