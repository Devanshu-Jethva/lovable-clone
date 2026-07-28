package com.devanshu.lovableclone.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devanshu.lovableclone.entity.UsageLog;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

	Optional<UsageLog> findByUsersIdAndDate(Long usersId, LocalDate date);
}
