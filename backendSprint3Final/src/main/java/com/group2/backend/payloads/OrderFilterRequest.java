package com.group2.backend.payloads;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.backend.model.Order;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderFilterRequest {
    private Order.OrderStatus status;
    private String dateRange; // "today", "this_week", "custom"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate requiredShippingDateStart;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate requiredShippingDateEnd;
    
    private String customerEmail;
    private Long userId;
    private Double minAmount;
    private String orderNumber;
   
    private Double maxAmount;
    private Order.PaymentStatus paymentStatus;
    private int page = 0;
    private int size = 10;
    private String sortBy = "orderDate";
    private String sortDirection = "desc";
}
