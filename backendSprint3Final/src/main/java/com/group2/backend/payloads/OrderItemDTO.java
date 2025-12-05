package com.group2.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long cookieId;
    private String cookieName;
    private int quantity;
    private double price;
    private double subtotal;
    private String imageUrl;
}
