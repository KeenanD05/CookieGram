package com.group2.backend.controllers;

import com.group2.backend.payloads.OrderItemRequest;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.service.OrderDateService;
import com.group2.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final OrderDateService orderDateService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            // Calculate total cookies in the order
            int totalCookies = orderRequest.getItems().stream()
                    .mapToInt(OrderItemRequest::getQuantity)
                    .sum();
            
            // Check if the requested date is available
            if (!orderDateService.isDateAvailable(
                    orderRequest.getRequiredShippingDate().toLocalDate(), 
                    totalCookies)) {
                LocalDate nextAvailable = orderDateService.getNextAvailableShippingDate(totalCookies);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Requested date is not available for the order");
                response.put("nextAvailableDate", nextAvailable);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            OrderResponse order = orderService.createOrder(orderRequest);
            
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(order.getId())
                    .toUri();
                
            return ResponseEntity.created(location).body(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Integer>> getCookieAvailability(
            @RequestParam(defaultValue = "7") int daysAhead) {
        LocalDate startDate = LocalDate.now();
        return ResponseEntity.ok(orderDateService.getCookieAvailability(startDate, daysAhead));
    }


    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }
    

}
