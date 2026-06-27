package com.devanshu.lovableclone.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record JwtUserPrincipal(
        Long id,
        String username,
        List<? extends GrantedAuthority> authorities
) {
}