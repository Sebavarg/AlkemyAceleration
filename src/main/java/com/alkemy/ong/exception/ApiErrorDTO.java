package com.alkemy.ong.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiErrorDTO {
    private String code;
    private String message;
    private ApiErrorDetailDTO details;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiErrorDetailDTO {
    private String field;
    private String error;

    public ApiErrorDetailDTO() {
    }
}
