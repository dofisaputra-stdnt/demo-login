package com.cloudify.demologin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String name;

    @NotNull
    private double price;

    @NotBlank
    private String description;
}
