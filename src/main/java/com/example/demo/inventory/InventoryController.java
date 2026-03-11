package com.example.demo.inventory;

import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/stores/{storeId}/inventory")
    public List<Inventory> listByStore(@PathVariable Long storeId) {
        return inventoryService.listByStore(storeId);
    }

    @PutMapping("/stores/{storeId}/inventory/{productId}")
    public Inventory upsert(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam @Min(0) int quantity
    ) {
        return inventoryService.upsert(storeId, productId, quantity);
    }

    @PostMapping("/stores/{storeId}/inventory/{productId}/adjust")
    public Inventory adjust(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam int delta
    ) {
        return inventoryService.adjust(storeId, productId, delta);
    }
}

