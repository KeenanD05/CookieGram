package com.group2.backend.controllers;

import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PublicOrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderResponse> getPublicOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return orderService.getAllOrders(page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public OrderResponse getPublicOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
