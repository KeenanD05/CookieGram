package com.group2.backend.controllers;

import com.group2.backend.config.PaginationConfig;
import com.group2.backend.exception.IllegalOperationException;
import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.payloads.OrderFilterRequest;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/staff/orders")
@Validated
public class StaffOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaginationConfig paginationConfig;

    /* =========================================================
       1) GET ALL ORDERS (TOP-LEVEL)
       ========================================================= */
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE)
            String sortDirection) {

        Page<OrderResponse> orders =
                orderService.getAllOrders(page, size, sortBy, sortDirection);

        return ResponseEntity.ok(orders);
    }

    /* =========================================================
       2) FILTER ORDERS (POST — FIXED)
       ========================================================= */
    @PostMapping("/filter")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> filterOrders(
            @Valid @RequestBody OrderFilterRequest filterRequest) {
    	System.out.println("🚀 FILTER ENDPOINT HIT (POST) — backend received request");
        Page<OrderResponse> results = orderService.filterOrders(filterRequest);

        return results.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(results);
    }

    /* =========================================================
       3) GET ORDERS FOR DATE
       ========================================================= */
    @GetMapping("/date")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<OrderResponse>> getOrdersForDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(orderService.getOrdersForDate(targetDate));
    }

    /* =========================================================
       4) GET ORDERS BY USER EMAIL
       ========================================================= */
    @GetMapping("/user")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserEmail(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE)
            String sortDirection,
            @RequestParam @NotBlank String email) {

        Page<OrderResponse> orders =
                orderService.getAllOrdersByUser(page, size, sortBy, sortDirection, email);

        return ResponseEntity.ok(orders);
    }

    /* =========================================================
       5) GET ORDER BY ID  (placed AFTER filter, BEFORE generic {id})
       ========================================================= */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable @Min(1) Long id) {

        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /* =========================================================
       6) UPDATE ORDER
       ========================================================= */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody OrderRequest request) {

        try {
            return ResponseEntity.ok(orderService.updateOrder(id, request));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /* =========================================================
       7) CHANGE STATUS (generic id — placed safely at bottom)
       ========================================================= */
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> changeOrderStatus(
            @PathVariable @Min(1) Long id,
            @RequestParam @NotBlank String orderStatus) {

        try {
            return ResponseEntity.ok(orderService.changeOrderStatus(id, orderStatus));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalOperationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* =========================================================
       8) CANCEL ORDER (most specific path — last)
       ========================================================= */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable @Min(1) Long id) {

        try {
            return ResponseEntity.ok(orderService.cancelOrder(id));
        } catch (IllegalOperationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
