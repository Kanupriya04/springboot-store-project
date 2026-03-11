package com.example.demo.inventory;

import com.example.demo.common.BadRequestException;
import com.example.demo.common.NotFoundException;
import com.example.demo.products.Product;
import com.example.demo.products.ProductService;
import com.example.demo.stores.Store;
import com.example.demo.stores.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreService storeService;
    private final ProductService productService;

    public InventoryService(InventoryRepository inventoryRepository, StoreService storeService, ProductService productService) {
        this.inventoryRepository = inventoryRepository;
        this.storeService = storeService;
        this.productService = productService;
    }

    public List<Inventory> listByStore(Long storeId) {
        storeService.get(storeId);
        return inventoryRepository.findByStoreId(storeId);
    }

    public Inventory get(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory record not found: " + id));
    }

    public Inventory upsert(Long storeId, Long productId, int quantity) {
        if (quantity < 0) throw new BadRequestException("Quantity must be >= 0");
        Store store = storeService.get(storeId);
        Product product = productService.get(productId);

        Inventory inv = inventoryRepository.findByStoreIdAndProductId(storeId, productId).orElseGet(Inventory::new);
        inv.setStore(store);
        inv.setProduct(product);
        inv.setQuantity(quantity);
        return inventoryRepository.save(inv);
    }

    public Inventory adjust(Long storeId, Long productId, int delta) {
        Inventory inv = inventoryRepository.findByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> new NotFoundException("Inventory record not found for store=" + storeId + ", product=" + productId));
        int newQty = inv.getQuantity() + delta;
        if (newQty < 0) throw new BadRequestException("Insufficient stock");
        inv.setQuantity(newQty);
        return inventoryRepository.save(inv);
    }
}

