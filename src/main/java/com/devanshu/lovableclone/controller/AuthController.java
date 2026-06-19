package com.devanshu.lovableclone.controller;

import com.devanshu.lovableclone.dto.GenericResponseHandler;
import com.devanshu.lovableclone.dto.auth.AuthResponseDTO;
import com.devanshu.lovableclone.dto.auth.LoginRequestDTO;
import com.devanshu.lovableclone.dto.auth.ProfileResponseDTO;
import com.devanshu.lovableclone.dto.auth.SignupRequestDTO;
import com.devanshu.lovableclone.service.AuthService;
import com.devanshu.lovableclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.signup(signupRequestDTO);
        return GenericResponseHandler.builder().message("Sign Up successfully").status(HttpStatus.OK)
                                     .data(authResponseDTO).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(loginRequestDTO);
        return GenericResponseHandler.builder().message("Login successfully").status(HttpStatus.OK)
                                     .data(authResponseDTO).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile() {
        Long userId = 1L; // TODO
        ProfileResponseDTO profileResponseDTO = userService.getProfile(userId);
        return GenericResponseHandler.builder().message("Profile fetched successfully")
                                     .status(HttpStatus.OK).data(profileResponseDTO).build();
    }

}
