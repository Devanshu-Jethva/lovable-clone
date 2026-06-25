package com.devanshu.lovableclone.mapper;

import com.devanshu.lovableclone.dto.projects.member.ProjectMemberDTO;
import com.devanshu.lovableclone.entity.ProjectMember;
import com.devanshu.lovableclone.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant = "OWNER")
    @Mapping(target = "invitedAt", ignore = true)
    ProjectMemberDTO toDto(Users owner);

    @Mapping(target = "userId", source = "users.id")
    @Mapping(target = "name", source = "users.name")
    @Mapping(target = "username", source = "users.username")
    ProjectMemberDTO toDto(ProjectMember projectMember);

    List<ProjectMemberDTO> toDtos(List<ProjectMember> projectMembers);
}
