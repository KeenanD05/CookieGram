package com.group2.backend.payloads;

import com.group2.backend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private AddressDTO shippingAddress;
    private List<OrderItemDTO> items;
    

    private String userEmail;
    private String formattedOrderDate;
    private String formattedTotal;
}
