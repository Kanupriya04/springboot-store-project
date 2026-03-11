package com.example.demo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByStoreId(Long storeId);
    Optional<Inventory> findByStoreIdAndProductId(Long storeId, Long productId);
}

