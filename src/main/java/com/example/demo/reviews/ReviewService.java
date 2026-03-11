package com.example.demo.reviews;

import com.example.demo.common.BadRequestException;
import com.example.demo.common.NotFoundException;
import com.example.demo.customers.Customer;
import com.example.demo.customers.CustomerService;
import com.example.demo.products.Product;
import com.example.demo.products.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, CustomerService customerService, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    public List<Review> list(Long productId, Long customerId) {
        if (productId != null) return reviewRepository.findByProductId(productId);
        if (customerId != null) return reviewRepository.findByCustomerId(customerId);
        return reviewRepository.findAll();
    }

    public Review get(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found: " + id));
    }

    public Review create(Long customerId, Long productId, Review review) {
        Customer customer = customerService.get(customerId);
        Product product = productService.get(productId);

        review.setId(null);
        review.setCustomer(customer);
        review.setProduct(product);

        try {
            return reviewRepository.save(review);
        } catch (Exception ex) {
            // unique constraint (customer, product)
            throw new BadRequestException("Customer already reviewed this product");
        }
    }

    public void delete(Long id) {
        Review existing = get(id);
        reviewRepository.delete(existing);
    }
}

