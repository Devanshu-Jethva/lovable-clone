package com.devanshu.lovableclone.mapper;

import com.devanshu.lovableclone.dto.projects.file.ProjectFileListDTO;
import com.devanshu.lovableclone.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

//    ProjectListDTO toDto(ProjectFile projectFile);

    List<ProjectFileListDTO> toProjectListDTO(List<ProjectFile> projectFileList);
}
