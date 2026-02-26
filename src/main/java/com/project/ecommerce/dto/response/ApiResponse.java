package com.project.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
     private int status;
     private String message;
     private LocalDateTime timestamp;
     private T data;
     private Integer totalPages;
     private Long totalElements;
}
