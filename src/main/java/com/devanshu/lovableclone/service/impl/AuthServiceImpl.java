package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.auth.AuthResponseDTO;
import com.devanshu.lovableclone.dto.auth.LoginRequestDTO;
import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.BadRequestException;
import com.devanshu.lovableclone.mapper.UsersMapper;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.security.JwtUtil;
import com.devanshu.lovableclone.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UsersRepository usersRepository;
    UsersMapper usersMapper;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        usersRepository.findByUsername(signupRequestDTO.username()).ifPresent(_ -> {
            throw new BadRequestException("Username is already in use");
        });

        Users users = usersMapper.toEntity(signupRequestDTO);
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users = usersRepository.save(users);

        return new AuthResponseDTO(jwtUtil.generateToken(users), usersMapper.toDto(users));
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Users users = usersRepository.findByUsername(loginRequestDTO.username())
                                     .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (users.getDeletedAt() != null) {
            throw new BadRequestException("Account deleted");
        }

        if (!passwordEncoder.matches(loginRequestDTO.password(), users.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new AuthResponseDTO(jwtUtil.generateToken(users), usersMapper.toDto(users));
    }
}
