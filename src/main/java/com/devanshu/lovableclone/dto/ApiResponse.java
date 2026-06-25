package com.devanshu.lovableclone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String message;
    private T data;

    private Integer pageNumber;
    private Integer totalPages;
    private Boolean hasPreviousPage;
    private Boolean hasNextPage;
    private Long totalCount;
    private Long pageSize;
}