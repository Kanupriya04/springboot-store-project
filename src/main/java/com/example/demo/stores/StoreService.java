package com.example.demo.stores;

import com.example.demo.common.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> list() {
        return storeRepository.findAll();
    }

    public Store get(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store not found: " + id));
    }

    public Store create(Store store) {
        store.setId(null);
        return storeRepository.save(store);
    }

    public Store update(Long id, Store updated) {
        Store existing = get(id);
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setEmail(updated.getEmail());
        return storeRepository.save(existing);
    }

    public void delete(Long id) {
        Store existing = get(id);
        storeRepository.delete(existing);
    }
}

