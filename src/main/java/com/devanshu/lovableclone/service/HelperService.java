package com.devanshu.lovableclone.service;

import com.devanshu.lovableclone.entity.Users;

public interface HelperService {
    Users getUser(Long id);

    Users getUserByEmail(String email);
}
