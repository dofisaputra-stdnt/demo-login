package com.cloudify.demologin.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
}
