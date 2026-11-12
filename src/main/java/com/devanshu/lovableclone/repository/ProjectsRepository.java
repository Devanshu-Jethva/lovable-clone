package com.devanshu.lovableclone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devanshu.lovableclone.entity.Projects;

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
