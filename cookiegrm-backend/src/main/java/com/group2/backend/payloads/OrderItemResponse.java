package com.group2.backend.payloads;

import com.group2.backend.model.OrderItem;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long cookieId;
    private String cookieName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;

    public static OrderItemResponse fromOrderItem(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setCookieId(orderItem.getCookie().getId());
        response.setCookieName(orderItem.getCookie().getName());
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());
        response.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());
        return response;
    }
}
