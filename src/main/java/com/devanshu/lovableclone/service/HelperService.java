package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.security.JwtUserPrincipal;

public interface HelperService {
    Users getUser(Long id);

    Users getUserByUsername(String username);

    JwtUserPrincipal checkForUserLogin();
}
