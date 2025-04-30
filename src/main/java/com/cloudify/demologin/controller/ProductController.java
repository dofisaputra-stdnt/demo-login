package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.BaseResponse;
import com.cloudify.demologin.dto.response.ProductResponse;
import com.cloudify.demologin.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        productService.addProduct(request);
        return ResponseEntity.ok(BaseResponse.builder().message("Product created successfully").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        ProductResponse product = productService.getProductById(UUID.fromString(id));
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Product retrieved successfully")
                        .data(product)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Products retrieved successfully")
                        .data(products)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request
    ) {
        productService.updateProduct(request, UUID.fromString(id));
        return ResponseEntity.ok(BaseResponse.builder().message("Product updated successfully").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(UUID.fromString(id));
        return ResponseEntity.ok(BaseResponse.builder().message("Product deleted successfully").build());
    }
}
