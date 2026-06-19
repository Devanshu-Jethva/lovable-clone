package com.devanshu.lovableclone.dto;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericResponseHandler {

    private static final String DATA = "data";
    private static final String MESSAGE = "message";
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String HAS_PREVIOUS_PAGE = "hasPreviousPage";
    private static final String HAS_NEXT_PAGE = "hasNextPage";
    private static final String TOTAL_COUNT = "totalCount";
    private static final String PAGE_SIZE = "pageSize";

    private final Object data;
    private final HttpStatus status;
    private final String message;

    private final Integer pageNumber;
    private final Integer totalPages;
    private final Boolean hasPreviousPage;
    private final Boolean hasNextPage;
    private final Long totalCount;
    private final Long pageSize;

    private GenericResponseHandler(final Builder builder) {
        this.data = builder.data;
        this.status = builder.status;
        this.message = builder.message;

        this.pageNumber = builder.pageNumber;
        this.totalPages = builder.totalPages;
        this.hasPreviousPage = builder.hasPreviousPage;
        this.hasNextPage = builder.hasNextPage;
        this.totalCount = builder.totalCount;
        this.pageSize = builder.pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Object data;
        private HttpStatus status = HttpStatus.OK;
        private String message;

        private Integer pageNumber;
        private Integer totalPages;
        private Boolean hasPreviousPage;
        private Boolean hasNextPage;
        private Long totalCount;
        private Long pageSize;

        public Builder data(final Object data) {
            this.data = data;
            return this;
        }

        public Builder status(final HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder page(final Page<?> page) {
            this.data = page.getContent();
            this.pageNumber = page.getNumber();
            this.totalPages = page.getTotalPages();
            this.hasPreviousPage = page.hasPrevious();
            this.hasNextPage = page.hasNext();
            this.totalCount = page.getTotalElements();
            this.pageSize = (long) page.getSize();

            return this;
        }

        public ResponseEntity<Object> build() {

            GenericResponseHandler response = new GenericResponseHandler(this);

            Map<String, Object> responseMap = new LinkedHashMap<>();

            if (response.message != null) {
                responseMap.put(MESSAGE, response.message);
            }

            if (response.data != null) {
                responseMap.put(DATA, response.data);
            }

            if (response.pageNumber != null) {
                responseMap.put(PAGE_NUMBER, response.pageNumber);
            }

            if (response.totalPages != null) {
                responseMap.put(TOTAL_PAGES, response.totalPages);
            }

            if (response.hasPreviousPage != null) {
                responseMap.put(HAS_PREVIOUS_PAGE, response.hasPreviousPage);
            }

            if (response.hasNextPage != null) {
                responseMap.put(HAS_NEXT_PAGE, response.hasNextPage);
            }

            if (response.totalCount != null) {
                responseMap.put(TOTAL_COUNT, response.totalCount);
            }

            if (response.pageSize != null) {
                responseMap.put(PAGE_SIZE, response.pageSize);
            }

            return ResponseEntity.status(response.status).body(responseMap);
        }
    }
}