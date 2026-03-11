package com.example.demo.products;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Product> list(@RequestParam(required = false) Long storeId) {
        return productService.list(storeId);
    }

    @GetMapping("/products/{id}")
    public Product get(@PathVariable Long id) {
        return productService.get(id);
    }

    @PostMapping("/stores/{storeId}/products")
    public Product create(@PathVariable Long storeId, @Valid @RequestBody Product product) {
        return productService.create(storeId, product);
    }

    @PutMapping("/products/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

