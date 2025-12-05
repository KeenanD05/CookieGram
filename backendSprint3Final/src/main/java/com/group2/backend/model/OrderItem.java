package com.group2.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    private int quantity;
    private double price;
    
    @Enumerated(EnumType.STRING)
  
    private IcingFlavor icingFlavor;

    @Column(length = 200)
    private String message;   // Optional custom message

}
