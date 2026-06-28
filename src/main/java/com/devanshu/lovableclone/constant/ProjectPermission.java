package com.devanshu.lovableclone.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProjectPermission {

    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),

    MANAGE_MEMBERS("project_members:manage"),
    VIEW_MEMBERS("project_members:view");

    private final String value;
}
