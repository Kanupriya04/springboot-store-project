package com.example.demo.customers;

import com.example.demo.common.BadRequestException;
import com.example.demo.common.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }

    public Customer get(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    public Customer create(Customer customer) {
        customer.setId(null);
        customerRepository.findByEmail(customer.getEmail()).ifPresent(c -> {
            throw new BadRequestException("Email already in use: " + customer.getEmail());
        });
        return customerRepository.save(customer);
    }

    public Customer update(Long id, Customer updated) {
        Customer existing = get(id);
        if (!existing.getEmail().equalsIgnoreCase(updated.getEmail())) {
            customerRepository.findByEmail(updated.getEmail()).ifPresent(c -> {
                throw new BadRequestException("Email already in use: " + updated.getEmail());
            });
        }
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        return customerRepository.save(existing);
    }

    public void delete(Long id) {
        Customer existing = get(id);
        customerRepository.delete(existing);
    }
}

