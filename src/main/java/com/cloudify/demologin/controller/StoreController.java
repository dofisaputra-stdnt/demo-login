package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.request.StoreRequest;
import com.cloudify.demologin.dto.response.BaseResponse;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.StoreResponse;
import com.cloudify.demologin.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequest request) {
        storeService.addStore(request);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Store created successfully")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllStores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<StoreResponse> stores = storeService.getAllStores(page, size);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Stores retrieved successfully")
                        .data(stores)
                        .build()
        );
    }
}
