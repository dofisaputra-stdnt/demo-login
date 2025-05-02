package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.config.kafka.producer.ProductEventProducer;
import com.cloudify.demologin.dto.request.ProductRequest;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.ProductResponse;
import com.cloudify.demologin.entity.Product;
import com.cloudify.demologin.repository.ProductRepository;
import com.cloudify.demologin.service.ProductService;
import com.cloudify.demologin.util.ImageUtil;
import com.cloudify.demologin.util.MinioUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MinioUtil minioUtil;
    private final ImageUtil imageUtil;
    private final ProductEventProducer productEventProducer;

    public ProductServiceImpl(
            ProductRepository productRepository, 
            MinioUtil minioUtil, 
            ImageUtil imageUtil,
            ProductEventProducer productEventProducer) {
        this.productRepository = productRepository;
        this.minioUtil = minioUtil;
        this.imageUtil = imageUtil;
        this.productEventProducer = productEventProducer;
    }

    @Value("${minio.bucket.product}")
    private String productBucket;

    @Transactional
    @Override
    public void addProduct(ProductRequest request, MultipartFile file) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        productRepository.save(product);
        saveProductImage(file, product);
        
        // Send Kafka event for product creation
        productEventProducer.sendProductCreatedEvent(product);
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
                .imageUrl(product.getImageUrl())
                .build();
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        Page<Product> products = productRepository.findAll(PageRequest.of(pageNumber, size));
        List<ProductResponse> productResponses = products.getContent().stream().map(
                product -> ProductResponse.builder()
                        .id(product.getId().toString())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imageUrl(product.getImageUrl())
                        .build()
        ).toList();
        return PageResponse.<ProductResponse>builder()
                .page(page)
                .size(size)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }

    @Transactional
    @Override
    public void updateProduct(ProductRequest product, MultipartFile file, UUID id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        saveProductImage(file, existingProduct);
        
        // Send Kafka event for product update
        productEventProducer.sendProductUpdatedEvent(existingProduct);
    }

    private void saveProductImage(MultipartFile file, Product existingProduct) {
        try {
            byte[] compressedFile = imageUtil.compressImage(file);
            String imageUrl = minioUtil.uploadFile(productBucket, existingProduct.getId().toString(), compressedFile);
            existingProduct.setImageUrl(imageUrl);
            productRepository.save(existingProduct);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        minioUtil.deleteFile(productBucket, product.getId().toString());
        productRepository.delete(product);
    }
}
