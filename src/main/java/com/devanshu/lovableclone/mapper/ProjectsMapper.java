package com.devanshu.lovableclone.mapper;

import com.devanshu.lovableclone.dto.projects.ProjectDetailDTO;
import com.devanshu.lovableclone.dto.projects.ProjectListDTO;
import com.devanshu.lovableclone.entity.Projects;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectsMapper {

    ProjectDetailDTO toDetailDto(Projects save);

    List<ProjectListDTO> toProjectListDTOs(List<Projects> projects);
}
