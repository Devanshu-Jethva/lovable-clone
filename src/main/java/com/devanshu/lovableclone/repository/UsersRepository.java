package com.devanshu.lovableclone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
}
