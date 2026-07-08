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
public class ChatSessionId implements Serializable {

    @Serial
    private static final long serialVersionUID = -4724750204725486532L;
    
    Long userId;
    Long projectId;
}
