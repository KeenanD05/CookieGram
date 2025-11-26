package com.group2.backend.service;

import com.group2.backend.model.Order;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    // Create a new order
    OrderResponse createOrder(OrderRequest orderRequest);


    @Transactional
    OrderResponse cancelOrder(Long orderId);

    @Transactional
    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);
}
