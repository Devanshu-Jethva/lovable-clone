package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    Optional<ProjectFile> findByProjects_IdAndPath(Long projectId, String cleanPath);

    List<ProjectFile> findByProjects_Id(Long id);

}
