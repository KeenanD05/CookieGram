package com.group2.backend.payloads;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

import java.util.List;

@Data
public class OrderRequest {
    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private String customerEmail;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @NotNull(message = "Required shipping date is required")
    private LocalDateTime requiredShippingDate;

    @Valid
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    private double totalAmount;
}
