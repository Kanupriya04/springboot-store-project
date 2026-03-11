package com.example.demo.products;

import com.example.demo.common.NotFoundException;
import com.example.demo.stores.Store;
import com.example.demo.stores.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;

    public ProductService(ProductRepository productRepository, StoreService storeService) {
        this.productRepository = productRepository;
        this.storeService = storeService;
    }

    public List<Product> list(Long storeId) {
        if (storeId == null) {
            return productRepository.findAll();
        }
        return productRepository.findByStoreId(storeId);
    }

    public Product get(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    public Product create(Long storeId, Product product) {
        product.setId(null);
        Store store = storeService.get(storeId);
        product.setStore(store);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = get(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        Product existing = get(id);
        productRepository.delete(existing);
    }
}

