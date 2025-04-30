package com.cloudify.demologin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String location;
}
