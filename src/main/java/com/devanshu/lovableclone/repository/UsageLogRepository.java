package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {
}
