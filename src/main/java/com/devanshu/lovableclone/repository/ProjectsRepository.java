package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    @Query("""
            select p
                from Projects p
                join ProjectMember pm on pm.projects.id = p.id
                where pm.users.id = :ownerId
                  and p.deletedAt IS NULL
                order by p.updatedAt desc
            """)
    List<Projects> getAllAccessibleProjects(Long ownerId);

}
