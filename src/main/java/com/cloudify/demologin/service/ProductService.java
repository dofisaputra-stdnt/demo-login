package com.cloudify.demologin.service;

import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductService {
    void addProduct(ProductRequest request, MultipartFile file);

    ProductResponse getProductById(UUID id);

    PageResponse<ProductResponse> getAllProducts(int page, int size);

    void updateProduct(ProductRequest product, MultipartFile file, UUID id);

    void deleteProduct(UUID id);
}
