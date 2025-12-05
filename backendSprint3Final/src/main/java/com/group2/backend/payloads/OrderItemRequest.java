package com.group2.backend.payloads;

import com.group2.backend.model.IcingFlavor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "Cookie ID is required")
    private Long cookieId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    private IcingFlavor icingFlavor;
    private String message;
}
