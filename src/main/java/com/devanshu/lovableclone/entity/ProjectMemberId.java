package com.devanshu.lovableclone.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectMemberId implements Serializable {

    @Serial
    private static final long serialVersionUID = -6282546962187598977L;

    Long userId;
    Long projectId;
}
