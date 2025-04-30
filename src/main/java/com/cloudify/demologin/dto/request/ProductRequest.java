package com.cloudify.demologin.dto.request;

import lombok.*;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private double price;
    private String description;
}
