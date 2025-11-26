package com.group2.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    public enum OrderStatus {
        PENDING, PROCESSING, DELIVERED, CANCELLED
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private LocalDateTime requiredShippingDate;
    
    @Column
    private LocalDateTime preparedAt;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    // User is optional - orders can be placed by guests
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String customerEmail;  // Required for both guest and registered users

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false, length = 500)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        this.orderNumber = "ORD" + System.currentTimeMillis();
        if (this.requiredShippingDate == null) {
            throw new IllegalStateException("Required shipping date must be set");
        }
        if (this.customerEmail == null || this.customerEmail.trim().isEmpty()) {
            throw new IllegalStateException("Customer email is required");
        }
        validateShippingDate();
    }
    
    public void validateShippingDate() {
        if (requiredShippingDate.isBefore(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0))) {
            throw new IllegalStateException("Orders must be placed at least one day in advance");
        }
    }
    
    public boolean canBeModified() {
        return !LocalDate.now().isEqual(requiredShippingDate.minusDays(1).toLocalDate());
    }

    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
