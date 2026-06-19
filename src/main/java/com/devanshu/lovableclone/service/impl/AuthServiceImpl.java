package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.dto.auth.AuthResponseDTO;
import com.devanshu.lovableclone.dto.auth.LoginRequestDTO;
import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;
import com.devanshu.lovableclone.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        return null;
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        return null;
    }
}
