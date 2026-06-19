package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    @Query("""
            select p from Project p
            where p.owner.id = :ownerId
            and p.deletedAt IS NULL
            order by id desc
            """)
    List<Projects> getAllAccessibleProjects(Long ownerId);
}
