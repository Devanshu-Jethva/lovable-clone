package com.devanshu.lovableclone.security;

import com.devanshu.lovableclone.constant.ProjectPermission;
import com.devanshu.lovableclone.repository.ProjectMemberRepository;
import com.devanshu.lovableclone.service.HelperService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityExpression {

    ProjectMemberRepository projectMemberRepository;
    HelperService helperService;

    private boolean hasPermission(Long projectId, ProjectPermission projectPermission) {
        JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, jwtUserPrincipal.id())
                                      .map(role -> role.getPermissions().contains(projectPermission))
                                      .orElse(false);
    }

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.DELETE);
    }

    public boolean canViewProjectMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageProjectMembers(Long projectId) {
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
    }
}
