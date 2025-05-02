package com.cloudify.demologin.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private LocalDateTime timestamp;
    private String eventType;
} 