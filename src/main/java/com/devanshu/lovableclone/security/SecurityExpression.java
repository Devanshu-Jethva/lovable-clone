package com.devanshu.lovableclone.security;

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

    public boolean canViewProject(Long projectId) {
        JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();

        projectMemberRepository.findRoleByProject
    }

    public boolean canEditProject(Long projectId) {

    }
}
