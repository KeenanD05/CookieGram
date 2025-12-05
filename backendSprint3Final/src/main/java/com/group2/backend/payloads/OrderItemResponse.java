package com.group2.backend.payloads;

import com.group2.backend.model.IcingFlavor;
import com.group2.backend.model.OrderItem;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long cookieId;
    private String cookieName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
    
    private String icing; 
    private String message;
    
    public static OrderItemResponse fromOrderItem(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setCookieId(orderItem.getCookie().getId());
        response.setCookieName(orderItem.getCookie().getName());
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());
        response.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());
        if (orderItem.getIcingFlavor() != null) {
            response.setIcing(orderItem.getIcingFlavor().name());
        }
        response.setMessage(orderItem.getMessage());
        response.setMessage(orderItem.getMessage());

        return response;
    }
}
