package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.entity.Projects;
import com.devanshu.lovableclone.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    @Query("""
            select p from Project p
            where p.owner.id = :ownerId
            and p.deletedAt IS NULL
            order by p.updatedAt desc
            """)
    List<Projects> getAllAccessibleProjects(Long ownerId);

    Optional<Projects> findByIdAndOwnerAndDeletedAtIsNull(Long id, Users owners);

}
