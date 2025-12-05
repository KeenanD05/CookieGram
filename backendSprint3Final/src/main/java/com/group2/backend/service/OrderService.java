package com.group2.backend.service;

import com.group2.backend.model.Order;
import com.group2.backend.payloads.OrderFilterRequest;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    // Create a new order
    OrderResponse createOrder(OrderRequest orderRequest);


    @Transactional
    OrderResponse cancelOrder(Long orderId);

    @Transactional
    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getAllOrders(int page, int size, String sortBy, String sortDirection);

    Page<OrderResponse> getAllOrdersByUser(Integer page, Integer size, String sortBy, String sortDirection, String email);
    
    Page<OrderResponse> filterOrders(OrderFilterRequest filterRequest);

    OrderResponse changeOrderStatus(Long id,String orderStatus);

    List<OrderResponse> getOrdersForDate(java.time.LocalDate targetDate);

    Double getTotalSaleOverAPeriod(String startDate, String endDate);

   Page<OrderResponse> getCancelledOrders(int page, int size, String sortBy, String sortDirection);
}
