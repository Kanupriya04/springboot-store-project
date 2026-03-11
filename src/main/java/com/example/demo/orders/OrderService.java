package com.example.demo.orders;

import com.example.demo.common.BadRequestException;
import com.example.demo.common.NotFoundException;
import com.example.demo.customers.Customer;
import com.example.demo.customers.CustomerService;
import com.example.demo.inventory.InventoryRepository;
import com.example.demo.orders.dto.CreateOrderItemRequest;
import com.example.demo.orders.dto.CreateOrderRequest;
import com.example.demo.products.Product;
import com.example.demo.products.ProductService;
import com.example.demo.stores.Store;
import com.example.demo.stores.StoreService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final InventoryRepository inventoryRepository;

    public OrderService(
            OrderRepository orderRepository,
            StoreService storeService,
            CustomerService customerService,
            ProductService productService,
            InventoryRepository inventoryRepository
    ) {
        this.orderRepository = orderRepository;
        this.storeService = storeService;
        this.customerService = customerService;
        this.productService = productService;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Order> list(Long storeId, Long customerId) {
        if (customerId != null) return orderRepository.findByCustomerId(customerId);
        if (storeId != null) return orderRepository.findByStoreId(storeId);
        return orderRepository.findAll();
    }

    public Order get(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    @Transactional
    public Order create(CreateOrderRequest req) {
        Store store = storeService.get(req.getStoreId());
        Customer customer = customerService.get(req.getCustomerId());

        Order order = new Order();
        order.setStore(store);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);

        for (CreateOrderItemRequest itemReq : req.getItems()) {
            Product product = productService.get(itemReq.getProductId());
            if (!product.getStore().getId().equals(store.getId())) {
                throw new BadRequestException("Product " + product.getId() + " does not belong to store " + store.getId());
            }

            var inv = inventoryRepository.findByStoreIdAndProductId(store.getId(), product.getId())
                    .orElseThrow(() -> new BadRequestException("No inventory for product " + product.getId()));
            if (inv.getQuantity() < itemReq.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product " + product.getId());
            }
            inv.setQuantity(inv.getQuantity() - itemReq.getQuantity());
            inventoryRepository.save(inv);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(itemReq.getQuantity());
            oi.setUnitPrice(product.getPrice() == null ? BigDecimal.ZERO : product.getPrice());
            order.getItems().add(oi);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order markPaid(Long id) {
        Order order = get(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay a cancelled order");
        }
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancel(Long id) {
        Order order = get(id);
        if (order.getStatus() == OrderStatus.CANCELLED) return order;

        // restock
        for (OrderItem item : order.getItems()) {
            var inv = inventoryRepository.findByStoreIdAndProductId(order.getStore().getId(), item.getProduct().getId())
                    .orElseThrow(() -> new BadRequestException("No inventory for product " + item.getProduct().getId()));
            inv.setQuantity(inv.getQuantity() + item.getQuantity());
            inventoryRepository.save(inv);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}

