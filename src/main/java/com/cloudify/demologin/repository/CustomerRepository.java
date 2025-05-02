package com.cloudify.demologin.repository;

import com.cloudify.demologin.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query("select c from Customer c where c.username = ?1 or c.email = ?1")
    Optional<Customer> findByUsername(String username);
    
    @Query("select c from Customer c where c.email = ?1")
    Optional<Customer> findByEmail(String email);
}
