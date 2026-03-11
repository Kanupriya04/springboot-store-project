package com.example.demo.reviews;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public List<Review> list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long customerId
    ) {
        return reviewService.list(productId, customerId);
    }

    @GetMapping("/reviews/{id}")
    public Review get(@PathVariable Long id) {
        return reviewService.get(id);
    }

    @PostMapping("/customers/{customerId}/products/{productId}/reviews")
    public Review create(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @Valid @RequestBody Review review
    ) {
        return reviewService.create(customerId, productId, review);
    }

    @DeleteMapping("/reviews/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}

