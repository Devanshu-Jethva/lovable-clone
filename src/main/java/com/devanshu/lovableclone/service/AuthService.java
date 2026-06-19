package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.dto.auth.AuthResponseDTO;
import com.devanshu.lovableclone.dto.auth.LoginRequestDTO;
import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;

public interface AuthService {
    AuthResponseDTO signup(SignupRequestDTO signupRequestDTO);

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
