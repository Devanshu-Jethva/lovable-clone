package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Users getUserByUsername(String email) {
        return usersRepository.findByUsername(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public JwtUserPrincipal checkForUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof JwtUserPrincipal _)) {
            throw new AuthenticationCredentialsNotFoundException("Please login first");
        }
        return (JwtUserPrincipal) authentication.getPrincipal();
    }
}
