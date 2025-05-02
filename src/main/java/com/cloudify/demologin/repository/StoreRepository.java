package com.cloudify.demologin.repository;

import com.cloudify.demologin.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
}
