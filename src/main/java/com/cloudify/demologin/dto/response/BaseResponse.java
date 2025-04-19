package com.cloudify.demologin.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
    private T data;
}
