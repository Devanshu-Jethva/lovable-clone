package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.Preview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreviewRepository extends JpaRepository<Preview, Long> {
}
