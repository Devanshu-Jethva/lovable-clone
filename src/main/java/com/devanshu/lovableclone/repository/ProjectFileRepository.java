package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
}
