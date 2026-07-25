package com.devanshu.lovableclone.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.entity.ProjectFile;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

//    ProjectListDTO toDto(ProjectFile projectFile);

	List<ProjectFileListDTO> toProjectListDTO(List<ProjectFile> projectFileList);
}
