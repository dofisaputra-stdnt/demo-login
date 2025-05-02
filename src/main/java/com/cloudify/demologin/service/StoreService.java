package com.cloudify.demologin.service;

import com.cloudify.demologin.dto.request.StoreRequest;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.StoreResponse;

public interface StoreService {
    void addStore(StoreRequest request);

    PageResponse<StoreResponse> getAllStores(int page, int size);
}
