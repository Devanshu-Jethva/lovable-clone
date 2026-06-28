package com.devanshu.lovableclone.repository;

import com.devanshu.lovableclone.constant.ProjectRole;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.ProjectMemberId;
import com.devanshu.lovableclone.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    boolean existsByProjects_IdAndUsers_Id(Long projectsId, Long usersId);

    List<ProjectMember> findByProjects_Id(Long projectId);

    Optional<ProjectMember> findByProjectsAndProjectRole(Projects projects, ProjectRole projectRole);

    @Query("""
            Select pm.projectRole from ProjectMember pm
            where pm.id.userId = :userId
            and pm.id.projectId = :projectId
            """)
    Optional<ProjectRole> findRoleByProjectIdAndUserId(Long projectId, Long userId);
}
