package com.example.demo.stores;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<Store> list() {
        return storeService.list();
    }

    @GetMapping("/{id}")
    public Store get(@PathVariable Long id) {
        return storeService.get(id);
    }

    @PostMapping
    public Store create(@Valid @RequestBody Store store) {
        return storeService.create(store);
    }

    @PutMapping("/{id}")
    public Store update(@PathVariable Long id, @Valid @RequestBody Store store) {
        return storeService.update(id, store);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        storeService.delete(id);
    }
}

