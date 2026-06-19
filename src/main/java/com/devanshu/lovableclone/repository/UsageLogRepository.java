package com.devanshu.lovableclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLogRepository, Long> {
}
