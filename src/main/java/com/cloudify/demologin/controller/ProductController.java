package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.BaseResponse;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.ProductResponse;
import com.cloudify.demologin.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            encoding = {
                                    @Encoding(
                                            name = "data",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "file",
                                            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                                    )
                            }
                    )
            )
    )
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("data") ProductRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        productService.addProduct(request, file);
        return ResponseEntity.ok(BaseResponse.builder().message("Product created successfully").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
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
        PageResponse<ProductResponse> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Products retrieved successfully")
                        .data(products)
                        .build()
        );
    }

    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            encoding = {
                                    @Encoding(
                                            name = "data",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "file",
                                            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                                    )
                            }
                    )
            )
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProduct(
            @Valid @RequestPart("data") ProductRequest request,
            @RequestPart("file") MultipartFile file,
            @PathVariable UUID id
    ) {
        productService.updateProduct(request, file, id);
        return ResponseEntity.ok(BaseResponse.builder().message("Product updated successfully").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(BaseResponse.builder().message("Product deleted successfully").build());
    }
}
