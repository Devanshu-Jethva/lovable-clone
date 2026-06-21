package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.service.HelperService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class HelperServiceImpl implements HelperService {

    UsersRepository usersRepository;

    @Override
    public Users getUser(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
