package com.example.demo.orders;

import com.example.demo.orders.dto.CreateOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> list(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long customerId
    ) {
        return orderService.list(storeId, customerId);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orderService.get(id);
    }

    @PostMapping
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @PostMapping("/{id}/pay")
    public Order pay(@PathVariable Long id) {
        return orderService.markPaid(id);
    }

    @PostMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
}

