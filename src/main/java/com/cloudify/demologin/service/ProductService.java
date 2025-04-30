package com.cloudify.demologin.service;

import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductService {
    void addProduct(ProductRequest request);

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getAllProducts(int page, int size);

    void updateProduct(ProductRequest product, UUID id);

    void deleteProduct(UUID id);
}
