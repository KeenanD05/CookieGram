package com.group2.backend.payloads;

import com.group2.backend.model.Order;
import lombok.Data;

import java.time.LocalDate;
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
    private ReceiverResponse receiver;
    private Long receiverId;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String receiverInstructions;
    private LocalDateTime requiredShippingDate;



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
        response.setRequiredShippingDate(order.getRequiredShippingDate());
        // Set the total amount from the order
        response.setTotalAmount(order.getTotalAmount());

        ReceiverResponse receiverResponse = new ReceiverResponse();
        receiverResponse.setId(order.getReceiver().getId());
        receiverResponse.setName(order.getReceiver().getName());
        receiverResponse.setPhoneNumber(order.getReceiver().getPhoneNumber());
        receiverResponse.setEmail(order.getReceiver().getEmail());
        receiverResponse.setSpecialInstructions(order.getReceiver().getSpecialInstructions());
        response.setReceiver(receiverResponse);
        
        // Set order items and calculate total if needed
        if (order.getItems() != null) {
            response.setItems(order.getItems().stream()
                    .map(OrderItemResponse::fromOrderItem)
                    .collect(Collectors.toList()));
            
            // Set receiver information if available

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
