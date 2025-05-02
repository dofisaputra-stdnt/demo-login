package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.request.StoreRequest;
import com.cloudify.demologin.dto.response.PageResponse;
import com.cloudify.demologin.dto.response.StoreResponse;
import com.cloudify.demologin.entity.Store;
import com.cloudify.demologin.repository.StoreRepository;
import com.cloudify.demologin.service.StoreService;
import org.flywaydb.core.Flyway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final JdbcTemplate jdbcTemplate;

    public StoreServiceImpl(StoreRepository storeRepository, JdbcTemplate jdbcTemplate) {
        this.storeRepository = storeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addStore(StoreRequest request) {
        Store store = new Store();
        store.setName(request.getName());
        store.setLocation(request.getLocation());

        String storeName = request.getName();
        String schemaName = storeName.trim().toLowerCase().replaceAll("\\s+", "_");
        String createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        jdbcTemplate.execute(createSchemaQuery);

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcTemplate.getDataSource())
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .load();

        flyway.migrate();
        storeRepository.save(store);
    }

    @Override
    public PageResponse<StoreResponse> getAllStores(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        Page<Store> stores = storeRepository.findAll(PageRequest.of(pageNumber, size));
        List<StoreResponse> storeResponses = stores.getContent().stream()
                .map(store -> StoreResponse.builder()
                        .id(store.getId().toString())
                        .name(store.getName())
                        .location(store.getLocation())
                        .build())
                .toList();

        return PageResponse.<StoreResponse>builder()
                .page(page)
                .size(size)
                .totalElements(stores.getTotalElements())
                .totalPages(stores.getTotalPages())
                .content(storeResponses)
                .build();
    }
}
