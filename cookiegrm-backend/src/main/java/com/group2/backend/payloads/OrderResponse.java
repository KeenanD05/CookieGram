package com.group2.backend.payloads;

import com.group2.backend.model.Order;
import com.group2.backend.model.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String customerEmail;
    private String customerName;
    private String shippingAddress;
    private String status;
    private String paymentStatus;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setCustomerName(order.getCustomerName());
        response.setShippingAddress(order.getShippingAddress());
        response.setStatus(order.getStatus().name());
        response.setPaymentStatus(order.getPaymentStatus().name());
        response.setOrderDate(order.getOrderDate());
        
        // Set the total amount from the order
        response.setTotalAmount(order.getTotalAmount());
        
        // Set order items and calculate total if needed
        if (order.getItems() != null) {
            response.setItems(order.getItems().stream()
                    .map(OrderItemResponse::fromOrderItem)
                    .collect(Collectors.toList()));
            
            // If total amount is not set, calculate it from items
            if (response.getTotalAmount() == null || response.getTotalAmount() == 0) {
                double calculatedTotal = order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum();
                response.setTotalAmount(calculatedTotal);
            }
        } else {
            response.setItems(Collections.emptyList());
        }
        
        return response;
    }
}
