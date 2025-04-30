package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.ProductResponse;
import com.cloudify.demologin.entity.Product;
import com.cloudify.demologin.repository.ProductRepository;
import com.cloudify.demologin.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        productRepository.save(product);
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        return productRepository.findAll(PageRequest.of(pageNumber, size))
                .map(product -> ProductResponse.builder()
                        .id(product.getId().toString())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .build());
    }

    @Override
    public void updateProduct(ProductRequest product, UUID id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
